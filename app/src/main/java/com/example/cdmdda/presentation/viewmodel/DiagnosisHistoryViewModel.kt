package com.example.cdmdda.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.R
import com.example.cdmdda.common.DiagnosisUiState
import com.example.cdmdda.data.UserApi
import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.example.cdmdda.data.repository.ImageRepository
import com.example.cdmdda.domain.usecase.GetDiagnosisHistoryUseCase
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.launch

class DiagnosisHistoryViewModel(
    private val locale: String,
    private val getDiagnosisHistoryUseCase: GetDiagnosisHistoryUseCase,
) : ViewModel() {

    // declare: Firebase(Auth)
    private val currentUser get() = UserApi.user

    val title: String
        get() {
            val userEmail = (currentUser?.email ?: String())
                .substringBefore("@")
            return when (locale) {
                "tl" -> "History ni $userEmail"
                else -> "$userEmail's History"
            }
        }

    private var options: FirestoreRecyclerOptions<DiagnosisUiState>? = null

    override fun onCleared() {
        options?.let { getDiagnosisHistoryUseCase.onViewModelCleared() }
        super.onCleared()
    }

    suspend fun getDiagnosisOptions(): FirestoreRecyclerOptions<DiagnosisUiState>? {
        options = currentUser?.let { getDiagnosisHistoryUseCase(it.uid, false) }
        return options
    }

    val uiDrawDividerY by lazy { R.drawable.divider_vertical }

    private val _loadingDiagnosisUiState = MutableLiveData(true)
    val loadingDiagnosisUiState: LiveData<Boolean> = _loadingDiagnosisUiState
    fun finishedLoadingDiagnosis() {
        _loadingDiagnosisUiState.value = false
    }

    private val _isEmptyDiagnosisUiState = MutableLiveData(true)
    val isEmptyDiagnosisUiState: LiveData<Boolean> = _isEmptyDiagnosisUiState
    fun finishedReturnedDiagnosis(isEmpty: Boolean) {
        _isEmptyDiagnosisUiState.value = isEmpty
    }
}