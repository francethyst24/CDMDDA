package com.example.cdmdda.presentation.viewmodel

import androidx.lifecycle.*
import com.example.cdmdda.R
import com.example.cdmdda.data.dto.Disease
import com.example.cdmdda.data.dto.DiseaseProfile
import com.example.cdmdda.data.dto.ImageResource
import com.example.cdmdda.domain.usecase.GetDiseaseProfileUseCase
import com.example.cdmdda.domain.usecase.GetOnlineImagesUseCase
import com.example.cdmdda.data.repository.DiseaseDataRepository
import kotlinx.coroutines.launch

class DiseaseProfileViewModel(
    private val disease: DiseaseDataRepository,
    private val getDiseaseProfileUseCase: GetDiseaseProfileUseCase,
    private val getOnlineImagesUseCase: GetOnlineImagesUseCase,
) : ViewModel() {
    val uiHeadCrops by lazy { R.string.ui_text_crops }

    fun diseaseUiState(dto: Disease) = liveData { emit(getDiseaseProfileUseCase(dto)) }

    private val _imageBitmaps = mutableListOf<ImageResource>()
    val imageBitmaps: List<ImageResource> = _imageBitmaps

    fun fetchDiseaseImages(disease: DiseaseProfile) {
        fetchOfflineImages(disease.offlineImages)
        viewModelScope.launch {
            getOnlineImagesUseCase(disease.id) {
                _imageBitmaps.add(ImageResource.Uri(it))
                _diseaseImages.postValue(_imageBitmaps.size - 1)
            }
        }
    }

    private fun fetchOfflineImages(offlineImages: IntArray) {
        offlineImages.forEach { _imageBitmaps.add(ImageResource.Res(it)) }
        /*val typedArray: TypedArray
        try {
            typedArray = obtainTypedArray(offlineImages)
        } catch (e: Resources.NotFoundException) {
            return
        }
        for (i in 0 until typedArray.length()) {
            val resId = typedArray.getResourceId(i, 0)
            _imageBitmaps.add(ImageResource.Res(resId))
        }
        typedArray.recycle()*/
    }

    private val _diseaseImages = MutableLiveData(_imageBitmaps.size - 1)
    val imageCount: LiveData<Int> = _diseaseImages

}