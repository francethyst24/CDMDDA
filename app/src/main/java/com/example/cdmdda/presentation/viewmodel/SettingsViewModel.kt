package com.example.cdmdda.presentation.viewmodel

import android.provider.SearchRecentSuggestions
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.domain.usecase.GetAuthStateUseCase
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getAuthStateUseCase: GetAuthStateUseCase,
) : ViewModel() {
    companion object {
        const val DEFAULT_THEME        = "default"
        const val DEFAULT_LANG         = "en"
        const val PREF_THEME           = "theme"
        const val PREF_LANG            = "lang"
        const val PREF_PERSONAL        = "personal"
        const val PREF_CLEAR_DIAGNOSIS = "clear_diagnosis"
        const val PREF_CLEAR_SEARCH    = "clear_search"
        const val PREF_LEARN_MORE      = "learn_more"
    }

    val user : FirebaseUser? get() = getAuthStateUseCase()

    fun clearDiagnosis(repository: DiagnosisRepository, updateUi: () -> Unit) {
        viewModelScope.launch { repository.deleteAll { updateUi() } }
    }

    fun clearSearch(
        repository: SearchQueryRepository,
        provider: SearchRecentSuggestions,
        updateUi: () -> Unit
    ) {
        repository.deleteCache(provider)
        viewModelScope.launch { repository.deleteAll { updateUi() } }
    }
}