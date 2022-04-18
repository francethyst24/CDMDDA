package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.common.Constants.CROP
import com.example.cdmdda.common.Constants.DISEASE
import com.example.cdmdda.common.Constants.FLAG_ACTIVITY_CLEAR_TOP
import com.example.cdmdda.common.Constants.ORIENTATION_X
import com.example.cdmdda.common.ContextUtils.intentWith
import com.example.cdmdda.data.dto.CropText
import com.example.cdmdda.data.dto.Disease
import com.example.cdmdda.data.dto.DiseaseProfile
import com.example.cdmdda.data.dto.ImageResource
import com.example.cdmdda.data.repository.OnlineImageRepository
import com.example.cdmdda.databinding.ActivityDiseaseProfileBinding
import com.example.cdmdda.domain.usecase.GetDiseaseProfileUseCase
import com.example.cdmdda.domain.usecase.GetOnlineImagesUseCase
import com.example.cdmdda.presentation.adapter.ImageAdapter
import com.example.cdmdda.presentation.adapter.StringAdapter
import com.example.cdmdda.presentation.adapter.TextViewLinksAdapter
import com.example.cdmdda.presentation.adapter.TextViewLinksAdapter.Companion.setAdapter
import com.example.cdmdda.presentation.helper.DiseaseResourceHelper
import com.example.cdmdda.presentation.viewmodel.DiseaseProfileViewModel
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory

class DiseaseProfileActivity : BaseCompatActivity() {
    companion object { const val TAG = "DiseaseProfileActivity" }

    // region // declare: ViewModel, ViewBinding
    private val layout: ActivityDiseaseProfileBinding by lazy {
        ActivityDiseaseProfileBinding.inflate(layoutInflater)
    }
    private val viewModel: DiseaseProfileViewModel by viewModels {
        CreateWithFactory {
            DiseaseProfileViewModel(
                diseaseResource,
                GetDiseaseProfileUseCase(diseaseResource),
                GetOnlineImagesUseCase(OnlineImageRepository(set))
            )
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

        val parcel = intent.getParcelableExtra(DISEASE) as Disease?
        parcel?.let { uiState ->
            viewModel.diseaseUiState(uiState).observe(this) { disease ->
                viewModel.fetchDiseaseImages(resources, disease.id)
                setImageRecycler(viewModel.imageBitmaps)
                viewModel.imageCount.observe(this) { imageAdapter?.notifyItemInserted(it) }

                // bind: DiseaseProfile -> UI
                bind(disease)
            }
        }
    }

    private fun bind(uiState: DiseaseProfile) = layout.apply {
        loadingDisease.hide()
        textDiseaseName.text = uiState.id
        supportActionBar?.title = uiState.id
        textDiseaseVector.text = uiState.vector
        textDiseaseCause.text = uiState.cause
        if (uiState.isSupported) iconDiseaseSupported.visibility = View.VISIBLE

        setSymptomRecycler(uiState.symptoms)
        setTreatmentRecycler(uiState.treatments)

        val header = "${getString(viewModel.uiHeadCrops)}: "
        textCropsAffected.text = header.plus(uiState.cropsAffected.joinToString { it.name })

        val adapter = TextViewLinksAdapter(uiState.cropsAffected, header.length-1) {
            val parcel = it as CropText
            startActivity(intentWith(extra = CROP, parcel).addFlags(FLAG_ACTIVITY_CLEAR_TOP))
        }
        textCropsAffected.setAdapter(adapter)
    }

    private fun setImageRecycler(images: List<ImageResource>) = layout.recyclerImages.let {
        imageAdapter = ImageAdapter(images)
        it.setHasFixedSize(false)
        it.isNestedScrollingEnabled = false
        it.layoutManager = LinearLayoutManager(this, ORIENTATION_X, false)
        it.adapter = imageAdapter
    }

    private fun setSymptomRecycler(symptoms: List<String>) = layout.recyclerSymptoms.let {
        it.setHasFixedSize(false)
        it.isNestedScrollingEnabled = false
        it.layoutManager = LinearLayoutManager(this)
        it.adapter = StringAdapter(symptoms)
    }

    private fun setTreatmentRecycler(treatments: List<String>) = layout.recyclerTreatments.let {
        it.setHasFixedSize(false)
        it.isNestedScrollingEnabled = false
        it.layoutManager = LinearLayoutManager(this)
        it.adapter = StringAdapter(treatments)
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