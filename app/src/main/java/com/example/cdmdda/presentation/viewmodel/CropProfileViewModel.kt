package com.example.cdmdda.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.data.dto.CropItemUiState
import com.example.cdmdda.data.dto.CropProfileUiState
import com.example.cdmdda.data.dto.CropTextUiState
import com.example.cdmdda.data.dto.CropUiState
import com.example.cdmdda.presentation.helper.ResourceHelper


class CropProfileViewModel(private val crop: CropUiState) : ViewModel() {

    suspend fun cropUiState(resourceHelper: ResourceHelper) : CropProfileUiState = when (crop) {
        is CropItemUiState -> viewModelScope.run {
            val cropItemUiState = crop
            val cropId = cropItemUiState.id
            CropProfileUiState(
                cropItemUiState.name,
                resourceHelper.fetchCropSciName(cropId),
                resourceHelper.fetchCropDescription(cropId),
                resourceHelper.fetchCropDiseasesAcquirable(cropId),
                cropItemUiState.isSupported,
                cropItemUiState.bannerId,
            )
        }
        is CropTextUiState -> viewModelScope.run {
            val cropTextUiState = crop
            val cropId = cropTextUiState.id
            CropProfileUiState(
                cropTextUiState.name,
                resourceHelper.fetchCropSciName(cropId),
                resourceHelper.fetchCropDescription(cropId),
                resourceHelper.fetchCropDiseasesAcquirable(cropId),
                resourceHelper.fetchCropIsSupported(cropId),
                resourceHelper.fetchCropBannerId(cropId),
            )
        }
    }

}


