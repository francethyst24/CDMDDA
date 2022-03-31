package com.example.cdmdda.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.common.AppData
import com.example.cdmdda.data.dto.CropTextUiState
import com.example.cdmdda.data.dto.DiseaseProfileUiState
import com.example.cdmdda.data.dto.DiseaseUiState
import com.example.cdmdda.data.dto.ImageResource
import com.example.cdmdda.data.repository.OnlineImageRepository
import com.example.cdmdda.databinding.ActivityDiseaseProfileBinding
import com.example.cdmdda.domain.usecase.GetDiseaseProfileUseCase
import com.example.cdmdda.domain.usecase.GetOnlineImagesUseCase
import com.example.cdmdda.presentation.adapter.ImageAdapter
import com.example.cdmdda.presentation.adapter.StringAdapter
import com.example.cdmdda.presentation.adapter.setAdapter
import com.example.cdmdda.presentation.helper.DiseaseResourceHelper
import com.example.cdmdda.presentation.adapter.TextViewLinksAdapter
import com.example.cdmdda.presentation.viewmodel.DiseaseProfileViewModel

class DiseaseProfileActivity : BaseCompatActivity() {
    companion object { const val TAG = "DiseaseProfileActivity" }

    // region // declare: ViewModel, ViewBinding
    private val layout: ActivityDiseaseProfileBinding by lazy {
        ActivityDiseaseProfileBinding.inflate(layoutInflater)
    }
    private val viewModel: DiseaseProfileViewModel by viewModels {
        object : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DiseaseProfileViewModel(
                    diseaseResource,
                    GetDiseaseProfileUseCase(diseaseResource),
                    GetOnlineImagesUseCase(OnlineImageRepository(set))
                ) as T
            }
        }
    }
    // endregion
    private val set: String by lazy { diseaseResource.dataset }
    private val diseaseResource: DiseaseResourceHelper by lazy { DiseaseResourceHelper(this) }
    private var imageAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(layout.toolbarDisplayDisease)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        val parcel = intent.getParcelableExtra("disease_id") as DiseaseUiState?
        parcel?.let { uiState ->
            viewModel.diseaseUiState(uiState).observe(this) { disease ->
                viewModel.fetchDiseaseImages(resources, disease.id)
                setImageRecycler(viewModel.imageBitmaps)
                viewModel.imageCount.observe(this) { imageAdapter?.notifyItemInserted(it) }

                // bind: DiseaseProfileUiState -> UI
                disease.bind()
            }
        }
    }

    private fun DiseaseProfileUiState.bind() = layout.apply {
        loadingDisease.hide()
        textDiseaseName.text = id
        supportActionBar?.title = id
        textDiseaseVector.text = vector
        textDiseaseCause.text = cause
        if (isSupported) iconDiseaseSupported.visibility = View.VISIBLE

        setSymptomRecycler(symptoms)
        setTreatmentRecycler(treatments)

        val header = "${getString(R.string.ui_text_crops)}: "
        textCropsAffected.text = header.plus(cropsAffected.joinToString { it.name })

        val adapter = TextViewLinksAdapter(cropsAffected, header.length-1) {
            val parcel = it as CropTextUiState
            val flag = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(interactivity(AppData.CROP, parcel).addFlags(flag))
        }
        textCropsAffected.setAdapter(adapter)
    }

    private fun setImageRecycler(images: List<ImageResource>) = layout.recyclerImages.apply {
        imageAdapter = ImageAdapter(images)
        setHasFixedSize(false)
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(this@DiseaseProfileActivity, LinearLayoutManager.HORIZONTAL, false)
        adapter = imageAdapter
    }

    private fun setSymptomRecycler(symptoms: List<String>) = layout.recyclerSymptoms.apply {
        setHasFixedSize(false)
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(this@DiseaseProfileActivity)
        adapter = StringAdapter(symptoms)
    }

    private fun setTreatmentRecycler(treatments: List<String>) = layout.recyclerTreatments.apply {
        setHasFixedSize(false)
        isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(this@DiseaseProfileActivity)
        adapter = StringAdapter(treatments)
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}