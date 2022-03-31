package com.example.cdmdda.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.data.dto.CropTextUiState
import com.example.cdmdda.data.dto.DiseaseTextUiState
import com.example.cdmdda.presentation.helper.ResourceHelper

class LearnMoreViewModel(private val helper: ResourceHelper) : ViewModel() {

    private val _cropUiStates = mutableListOf<CropTextUiState>()
    val cropUiStates = _cropUiStates
    fun cropCount() = liveData {
        helper.supportedCrops.forEach {
            _cropUiStates.add(CropTextUiState(it, helper.name(id = it)))
            emit(cropUiStates.size - 1)
        }
    }

    private val _diseaseUiStates = mutableListOf<DiseaseTextUiState>()
    val diseaseUiStates = _diseaseUiStates
    fun diseaseCount() = liveData {
        helper.supportedDiseases.forEach {
            _diseaseUiStates.add(DiseaseTextUiState(it))
            emit(diseaseUiStates.size - 1)
        }
    }
}