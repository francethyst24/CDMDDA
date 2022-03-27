package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.data.dto.*
import com.example.cdmdda.data.repository.OnlineImageRepository
import com.example.cdmdda.presentation.helper.ResourceHelper
import kotlinx.coroutines.launch

class DiseaseProfileViewModel(
    private val disease: DiseaseUiState,
    private val imageRepository: OnlineImageRepository,
) : ViewModel(), OnlineImageRepository.OnlineImageListener {

    suspend fun diseaseUiState(resolver: ResourceHelper): DiseaseProfileUiState = resolver.run {
        val id = disease.id
        return when(disease) {
            is DiseaseItemUiState -> viewModelScope.run {
                DiseaseProfileUiState(
                    id,
                    fetchDiseaseVector(id),
                    fetchDiseaseCause(id),
                    fetchDiseaseSymptoms(id),
                    fetchDiseaseTreatment(id),
                    fetchDiseaseCropsAffected(id),
                    disease.isSupported,
                )
            }
            is DiseaseTextUiState, is DiseaseDiagnosisUiState -> viewModelScope.run {
                DiseaseProfileUiState(
                    id,
                    fetchDiseaseVector(id),
                    fetchDiseaseCause(id),
                    fetchDiseaseSymptoms(id),
                    fetchDiseaseTreatment(id),
                    fetchDiseaseCropsAffected(id),
                    fetchDiseaseIsSupported(id)
                )
            }
        }
    }

    private val _imageBitmaps = mutableListOf<ImageResource>()
    val imageBitmaps : List<ImageResource> = _imageBitmaps

    fun fetchDiseaseImages(context: Context) {
        fetchOfflineImages(context.resources, ResourceHelper(context))
        viewModelScope.launch {
            imageRepository.setImageRepositoryListener(this@DiseaseProfileViewModel)
            imageRepository.fetchOnlineImages(disease.id)
        }
    }

    private fun fetchOfflineImages(resources: Resources, resolver: ResourceHelper) {
        val typedArray : TypedArray
        resolver.fetchDiseaseImages(disease.id).also {
            try {
                typedArray = resources.obtainTypedArray(it)
            } catch (e: Resources.NotFoundException) {
                return
            }
        }
        for (i in 0 until typedArray.length()) {
            val id = typedArray.getResourceId(i, 0)
            _imageBitmaps.add(ImageResource.Integer(id))
        }
        typedArray.recycle()
    }

    private val _diseaseImages = MutableLiveData(_imageBitmaps.size - 1)
    val imageCount: LiveData<Int> get() = _diseaseImages

    override fun onOnlineImageReceived(onlineImage: Uri) {
        _imageBitmaps.add(ImageResource.Uri(onlineImage))
        _diseaseImages.postValue(_imageBitmaps.size - 1)
    }

}