package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.cdmdda.R
import com.example.cdmdda.data.repository.DiseaseRepository
import com.example.cdmdda.data.repository.ImageRepository
import com.example.cdmdda.domain.model.Image
import com.google.firebase.storage.StorageReference
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

    private var hasFetchedOfflineImages = false
    private var hasFetchedOnlineImages = false

    private val _imageBitmaps = mutableListOf<Image>()
    val imageBitmaps: List<Image> = _imageBitmaps

    private val _diseaseImages = MutableLiveData(_imageBitmaps.size - 1)
    val imageCount: LiveData<Int> = _diseaseImages

    fun diseaseUiState(context: Context, id: String) = liveData {
        if (!hasFetchedOnlineImages) {
            imageRepository.fetchOnlineImages(id) { onImageReceived(it) }
            hasFetchedOnlineImages = true
        }
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
        if (!hasFetchedOfflineImages) {
            _imageBitmaps.addAll(uiState.offlineImages)
            hasFetchedOfflineImages = true
        }
        emit(uiState)
    }

    private fun onImageReceived(list: List<StorageReference>) = viewModelScope.launch {
        list.forEach { item ->
            item.downloadUrl.addOnSuccessListener {
                _imageBitmaps.add(Image.Uri(it))
                _diseaseImages.postValue(_imageBitmaps.lastIndex)
            }
        }
    }

}