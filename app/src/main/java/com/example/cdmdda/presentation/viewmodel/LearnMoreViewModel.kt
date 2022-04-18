package com.example.cdmdda.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.data.dto.CropText
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.presentation.helper.ResourceHelper

class LearnMoreViewModel(private val helper: ResourceHelper) : ViewModel() {

    private val _cropUiStates = mutableListOf<CropText>()
    val cropUiStates = _cropUiStates
    fun cropCount() = liveData {
        val supported = helper.supportedCrops
        if (cropUiStates.size == supported.size) return@liveData
        supported.forEach { id ->
            val newCrop = CropText(id, helper.name(id))
            _cropUiStates.add(newCrop)
            _cropUiStates.sortBy { it.displayName }
            emit(_cropUiStates.indexOf(newCrop))
        }
    }

    private val _diseaseUiStates = mutableListOf<DiseaseText>()
    val diseaseUiStates = _diseaseUiStates
    fun diseaseCount() = liveData {
        val supported = helper.supportedDiseases
        if (diseaseUiStates.size == supported.size) return@liveData
        supported.forEach {
            _diseaseUiStates.add(DiseaseText(it))
            emit(diseaseUiStates.size - 1)
        }
    }
}