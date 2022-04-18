package com.example.cdmdda.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.data.dto.Crop
import com.example.cdmdda.domain.usecase.GetCropProfileUseCase


class CropProfileViewModel(private val getCropProfileUseCase: GetCropProfileUseCase) : ViewModel() {
    val uiHeadDiseases by lazy { R.string.ui_text_diseases }

    fun cropUiState(dto: Crop) = liveData { emit(getCropProfileUseCase(dto)) }

}


