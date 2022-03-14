package com.example.cdmdda.view

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivityDisplayDiseaseBinding
import com.example.cdmdda.view.adapter.ImageDataAdapter
import com.example.cdmdda.view.adapter.StringDataAdapter
import com.example.cdmdda.view.utils.ListenerUtils.attachListeners
import com.example.cdmdda.view.utils.generateLinks
import com.example.cdmdda.viewmodel.DisplayDiseaseViewModel
import com.example.cdmdda.viewmodel.factory.DisplayDiseaseViewModelFactory as ViewModelFactory

class DisplayDiseaseActivity : BaseCompatActivity() {

    // region // declare: ViewModel, ViewBinding
    private lateinit var layout: ActivityDisplayDiseaseBinding
    private lateinit var viewModel: DisplayDiseaseViewModel
    // endregion

    private var imageAdapter: ImageDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val diseaseId = intent.getStringExtra("disease_id")!!

        // region // init: ViewModel, ViewBinding
        viewModel = ViewModelProvider(this, ViewModelFactory(application, diseaseId))
            .get(DisplayDiseaseViewModel::class.java)
        viewModel.fetchDiseaseImages()

        layout = ActivityDisplayDiseaseBinding.inflate(layoutInflater).apply {
            setSupportActionBar(toolbarDisplayDisease)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                title = viewModel.disease(this@DisplayDiseaseActivity).name
            }
            setContentView(root)
        }
        // endregion

        setImageRecycler(viewModel.imageBitmaps)
        viewModel.diseaseImages.observe(this@DisplayDiseaseActivity) {
            imageAdapter?.notifyItemInserted(it)
            Log.i("DisplayDiseaseActivity", "notifyItemInserted($it), imageBitmaps.size=${viewModel.imageBitmaps.size}")
        }

        // bind: Disease -> UI
        viewModel.disease(this).also {
            layout.apply {
                loadingDisease.hide()
                textDiseaseName.text = it.name
                textDiseaseVector.text = it.vector
                textDiseaseCause.text = it.cause
                setSymptomRecycler(it.symptoms)
                setTreatmentRecycler(it.treatments)

                val cropHeader = "${getString(R.string.ui_text_crops)}: "
                textCropsAffected.text = cropHeader.plus(it.cropNames.joinToString())
                textCropsAffected.generateLinks(
                    cropHeader.length - 1,
                    *(getCropListeners(it.cropIds, it.cropNames)).toTypedArray()
                )

                viewModel.diseaseSupported(this@DisplayDiseaseActivity).observe(this@DisplayDiseaseActivity) { isSupported ->
                    if (isSupported) {
                        iconDiseaseSupported.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setImageRecycler(diseaseImages: List<Bitmap>) {
        imageAdapter = ImageDataAdapter(diseaseImages)
        layout.recyclerImages.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@DisplayDiseaseActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }

    private fun setSymptomRecycler(symptoms: List<String>) {
        layout.recyclerSymptoms.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@DisplayDiseaseActivity)
            adapter = StringDataAdapter(symptoms)
        }
    }

    private fun setTreatmentRecycler(treatments: List<String>) {
        layout.recyclerTreatments.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(this@DisplayDiseaseActivity)
            adapter = StringDataAdapter(treatments)
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