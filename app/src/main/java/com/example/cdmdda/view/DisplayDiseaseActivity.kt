package com.example.cdmdda.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivityDisplayDiseaseBinding
import com.example.cdmdda.model.TextRepository
import com.example.cdmdda.view.adapter.ImageDataAdapter
import com.example.cdmdda.view.adapter.SymptomDataAdapter
import com.example.cdmdda.view.utils.StringUtils.attachListeners
import com.example.cdmdda.view.utils.generateLinks
import com.example.cdmdda.viewmodel.DisplayDiseaseViewModel
import com.example.cdmdda.viewmodel.factory.DisplayDiseaseViewModelFactory as ViewModelFactory

class DisplayDiseaseActivity : BaseCompatActivity() {

    // region // declare: ViewModel, ViewBinding
    private lateinit var layout: ActivityDisplayDiseaseBinding
    private lateinit var viewModel: DisplayDiseaseViewModel
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val diseaseId = intent.getStringExtra("disease_id")!!

        // region // init: ViewModel, ViewBinding
        viewModel = ViewModelProvider(this, ViewModelFactory(application, diseaseId, TextRepository(this)))
            .get(DisplayDiseaseViewModel::class.java)
        viewModel.fetchDiseaseImages()

        layout = ActivityDisplayDiseaseBinding.inflate(layoutInflater).apply {
            setSupportActionBar(toolbarDisplayDisease)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = viewModel.disease.name
            }
            setContentView(root)
        }
        // endregion

        // bind: Disease -> UI
        viewModel.disease.also {
            layout.apply {
                loadingDisease.hide()
                textDiseaseName.text = it.name
                textDiseaseVector.text = it.vector
                textDiseaseTreatment.text = it.treatment
                textDiseaseCause.text = it.cause
                setSymptomRecycler(it.symptoms)

                val cropHeader = "${getString(R.string.ui_text_crops)}: "
                textCropsAffected.text = cropHeader.plus(it.cropNames.joinToString())
                textCropsAffected.generateLinks(
                    cropHeader.length - 1,
                    *(getCropListeners(it.cropIds, it.cropNames)).toTypedArray()
                )

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
            isNestedScrollingEnabled = false
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

    private fun getCropListeners(cropIds: List<String>, cropNames: List<String>) = attachListeners(this@DisplayDiseaseActivity, cropIds, cropNames)


    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@DisplayDiseaseActivity.finish(); true
        }
        else ->
            super.onOptionsItemSelected(item)
    }

}