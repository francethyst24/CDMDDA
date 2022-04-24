package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.data.dto.CropUiState
import com.example.cdmdda.data.repository.CropRepository


class CropProfileViewModel : ViewModel() {
    val uiHeadDiseases by lazy { R.string.ui_text_diseases }

    fun cropUiState(context: Context, dto: CropUiState) = liveData {
        val repository = CropRepository(context, dto.id)
        emit(repository.getCrop(dto))
    }

}


