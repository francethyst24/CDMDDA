package com.example.cdmdda.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.data.dto.CropTextUiState
import com.example.cdmdda.data.dto.DiseaseTextUiState
import com.example.cdmdda.presentation.helper.ResourceHelper

class LearnMoreViewModel : ViewModel() {

    private val _cropUiStates = mutableListOf<CropTextUiState>()
    val cropUiStates = _cropUiStates
    fun cropCount(resourceHelper: ResourceHelper) = liveData {
        resourceHelper.fetchCropsSupported().forEach {
            _cropUiStates.add(
                CropTextUiState(it, resourceHelper.fetchCropName(it))
            )
            emit(cropUiStates.size - 1)
        }
    }

    private val _diseaseUiStates = mutableListOf<DiseaseTextUiState>()
    val diseaseUiStates = _diseaseUiStates
    fun diseaseCount(resourceHelper: ResourceHelper) = liveData {
        resourceHelper.fetchDiseasesSupported().forEach {
            _diseaseUiStates.add(DiseaseTextUiState(it))
            emit(diseaseUiStates.size - 1)
        }
    }
}