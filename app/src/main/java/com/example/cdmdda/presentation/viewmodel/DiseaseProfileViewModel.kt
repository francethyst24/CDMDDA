package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.cdmdda.R
import com.example.cdmdda.data.repository.DiseaseRepository
import com.example.cdmdda.data.repository.ImageRepository
import com.example.cdmdda.domain.model.Image
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiseaseProfileViewModel(
    private val imageRepository: ImageRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    val uiHeadCrops by lazy { R.string.ui_text_crops }

    private val _imageBitmaps = mutableListOf<Image>()
    val imageBitmaps: List<Image> = _imageBitmaps

    private val _diseaseImages = MutableLiveData(_imageBitmaps.size - 1)
    val imageCount: LiveData<Int> = _diseaseImages

    fun diseaseUiState(context: Context, id: String) = liveData {
        imageRepository.fetchOnlineImages(id) { onImageReceived(it) }
        val uiState = withContext(ioDispatcher) {
            val diseaseRepository = DiseaseRepository(context, id)
            val images = withContext(defaultDispatcher) {
                imageRepository.fetchOfflineImages(
                    diseaseRepository.imagesArray,
                    context.resources,
                )
            }
            return@withContext diseaseRepository.getDisease(images)
        }
        _imageBitmaps.addAll(uiState.offlineImages)
        emit(uiState)
    }

    private fun onImageReceived(image: Image) = viewModelScope.launch {
        _imageBitmaps.add(image)
        _diseaseImages.postValue(_imageBitmaps.lastIndex)
    }

}