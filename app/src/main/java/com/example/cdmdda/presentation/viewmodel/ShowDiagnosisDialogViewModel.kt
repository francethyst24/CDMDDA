package com.example.cdmdda.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.R
import com.example.cdmdda.data.UserApi
import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.example.cdmdda.data.repository.ImageRepository
import kotlinx.coroutines.launch

class ShowDiagnosisDialogViewModel constructor(
    private val imageRepository: ImageRepository,
): ViewModel() {
    private val currentUser get() = UserApi.user

    private val _currentDiagnosisUiState = MutableLiveData<DiseaseDiagnosis?>(null)
    val currentDiagnosisUiState: LiveData<DiseaseDiagnosis?> = _currentDiagnosisUiState

    private val _diagnosisImageUiState = MutableLiveData<Uri?>(null)
    val diagnosisImageUiState: LiveData<Uri?> = _diagnosisImageUiState

    fun persistClickedDiagnosis(diagnosis: DiseaseDiagnosis) = viewModelScope.launch {
        _currentDiagnosisUiState.value = diagnosis
        currentUser?.let { user ->
            imageRepository.fetchDiagnosable(user.uid, diagnosis.documentId) {
                _diagnosisImageUiState.value = it
            }
        }
    }

    fun clearCachedDiagnosis() {
        _currentDiagnosisUiState.value = null
        _diagnosisImageUiState.value = null
    }

    val uiTextLearnMore by lazy { R.string.ui_text_learn_more }
}