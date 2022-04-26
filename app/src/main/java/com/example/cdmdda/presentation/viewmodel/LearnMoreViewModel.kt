package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.common.utils.AndroidUtils.getStringArray
import com.example.cdmdda.common.Constants.SUPPORTED_CROPS
import com.example.cdmdda.common.Constants.SUPPORTED_DISEASES
import com.example.cdmdda.data.dto.CropText
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.data.repository.CropRepository

class LearnMoreViewModel : ViewModel() {

    private val _cropUiStates = mutableListOf<CropText>()
    val cropUiStates = _cropUiStates
    fun cropCount(context: Context) = liveData {
        val supported = context.getStringArray(SUPPORTED_CROPS)
        if (cropUiStates.size == supported.size) return@liveData
        supported.forEach { id ->
            val newCrop = CropRepository(context, id).getText()
            _cropUiStates.add(newCrop)
            _cropUiStates.sortBy { it.displayName(context) }
            emit(_cropUiStates.indexOf(newCrop))
        }
    }

    private val _diseaseUiStates = mutableListOf<DiseaseText>()
    val diseaseUiStates = _diseaseUiStates
    fun diseaseCount(context: Context) = liveData {
        val supported = context.getStringArray(SUPPORTED_DISEASES)
        if (diseaseUiStates.size == supported.size) return@liveData
        supported.forEach {
            _diseaseUiStates.add(DiseaseText(it))
            emit(diseaseUiStates.size - 1)
        }
    }
}