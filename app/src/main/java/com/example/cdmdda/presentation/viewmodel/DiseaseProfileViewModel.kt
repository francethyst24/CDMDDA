package com.example.cdmdda.presentation.viewmodel

import android.content.res.Resources
import android.content.res.TypedArray
import androidx.lifecycle.*
import com.example.cdmdda.data.dto.Disease
import com.example.cdmdda.data.dto.ImageResource
import com.example.cdmdda.domain.usecase.GetDiseaseProfileUseCase
import com.example.cdmdda.domain.usecase.GetOnlineImagesUseCase
import com.example.cdmdda.presentation.helper.DiseaseResourceHelper
import kotlinx.coroutines.launch

class DiseaseProfileViewModel(
    private val disease: DiseaseResourceHelper,
    private val getDiseaseProfileUseCase: GetDiseaseProfileUseCase,
    private val getOnlineImagesUseCase: GetOnlineImagesUseCase,
) : ViewModel() {

    fun diseaseUiState(dto: Disease) = liveData { emit(getDiseaseProfileUseCase(dto)) }

    private val _imageBitmaps = mutableListOf<ImageResource>()
    val imageBitmaps : List<ImageResource> = _imageBitmaps

    fun fetchDiseaseImages(resources: Resources, id: String) {
        fetchOfflineImages(resources, id)
        viewModelScope.launch {
            getOnlineImagesUseCase(id) {
                _imageBitmaps.add(ImageResource.Uri(it))
                _diseaseImages.postValue(_imageBitmaps.size - 1)
            }
        }
    }

    private fun fetchOfflineImages(resources: Resources, id: String) {
        val typedArray : TypedArray
        try { typedArray = resources.obtainTypedArray(disease.offlineImages(id)) }
        catch (e: Resources.NotFoundException) { return }
        for (i in 0 until typedArray.length()) {
            val resId = typedArray.getResourceId(i, 0)
            _imageBitmaps.add(ImageResource.Integer(resId))
        }
        typedArray.recycle()
    }

    private val _diseaseImages = MutableLiveData(_imageBitmaps.size - 1)
    val imageCount: LiveData<Int> = _diseaseImages

}