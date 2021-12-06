package com.example.cdmdda.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivityDisplayDiseaseBinding
import com.example.cdmdda.model.dto.Disease
import com.example.cdmdda.view.adapter.ImageDataAdapter
import com.example.cdmdda.view.adapter.SymptomDataAdapter
import com.example.cdmdda.view.utils.LocaleUtils
import com.example.cdmdda.view.utils.TextViewUtils
import com.example.cdmdda.viewmodel.DisplayDiseaseViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.ListResult
import kotlinx.coroutines.*
import com.example.cdmdda.viewmodel.factory.DisplayDiseaseViewModelFactory as ViewModelFactory

class DisplayDiseaseActivity : BaseCompatActivity() {
    companion object {
        private const val TAG = "DisplayDiseaseActivity"
    }

    // region // declare: ViewModel, ViewBinding
    private lateinit var layout: ActivityDisplayDiseaseBinding
    private lateinit var viewModel: DisplayDiseaseViewModel
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val diseaseId = intent.getStringExtra("disease_id")!!

        // region // init: ViewModel, ViewBinding
        viewModel = ViewModelProvider(this, ViewModelFactory(application, diseaseId))
            .get(DisplayDiseaseViewModel::class.java)

        layout = ActivityDisplayDiseaseBinding.inflate(layoutInflater).apply {
            setSupportActionBar(toolbarDisplayDisease)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                viewModel.disease.observe(this@DisplayDiseaseActivity) { title = it.name }
            }
            setContentView(root)
        }
        // endregion

        // bind: Disease -> UI
        viewModel.disease.observe(this@DisplayDiseaseActivity) {
            layout.apply {
                loadingDisease.hide()
                textDisplayDiseaseName.text = it.name
                textDisplayDiseaseVector.text = it.vector
                textDescTreatment.text = it.treatment
                textDescCause.text = it.cause
                setSymptomRecycler(it.symptoms)

                val cropHeader = getString(R.string.text_crops)
                textCrops.text = cropHeader.plus(it.getCrops(this@DisplayDiseaseActivity).joinToString())
                TextViewUtils.generateLinks(
                    textCrops, cropHeader.length - 1, *(getCropListeners(it)).toTypedArray()
                )

                viewModel.fetchDiseaseImages()
                viewModel.diseaseImages.observe(this@DisplayDiseaseActivity) { diseaseImages ->
                    setImageRecycler(diseaseImages)
                }
            }
        }
    }

    private fun setImageRecycler(diseaseImages: List<Bitmap>) {
        val imageAdapter = ImageDataAdapter(diseaseImages)
        layout.recyclerImages.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@DisplayDiseaseActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }

    private fun setSymptomRecycler(symptoms: List<String>) {
        val symptomAdapter = SymptomDataAdapter(symptoms)
        layout.recyclerSymptoms.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@DisplayDiseaseActivity)
            adapter = symptomAdapter
        }
    }

    private fun getCropListeners(disease: Disease) = when (LocaleUtils.getLanguage(this@DisplayDiseaseActivity)) {
        "en" ->
            TextViewUtils.attachListeners(this@DisplayDiseaseActivity, disease.crops)
        else ->
            TextViewUtils.attachListeners(this@DisplayDiseaseActivity, disease.crops, disease.tl_crops)
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@DisplayDiseaseActivity.finish(); true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}