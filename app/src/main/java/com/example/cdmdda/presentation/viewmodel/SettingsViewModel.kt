package com.example.cdmdda.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.R
import com.example.cdmdda.data.UserApi
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    companion object {
        const val DEFAULT_THEME        = "default"
        const val DEFAULT_LOCAL         = "en"
        const val PREF_THEME           = "theme"
        const val PREF_LOCAL            = "lang"
        const val PREF_PERSONAL        = "personal"
        const val PREF_CLEAR_DIAGNOSIS = "clear_diagnosis"
        const val PREF_CLEAR_SEARCH    = "clear_search"
        const val PREF_LEARN_MORE      = "learn_more"
    }
    // Activity
    val uiSettings by lazy { R.id.settings }
    val uiInfoClearDiagnosisSuccess by lazy { R.string.ui_text_clear_diagnosis_success }
    val uiInfoClearSearchSuccess by lazy { R.string.ui_text_clear_search_success }
    val uiHeadSettings by lazy { R.string.ui_text_settings }
    // Fragment
    val xmlRoot by lazy { R.xml.root_preferences }

    val user : FirebaseUser? get() = UserApi.user

    fun clearDiagnosis(repository: DiagnosisRepository, updateUi: () -> Unit) {
        viewModelScope.launch { repository.deleteAll { updateUi() } }
    }

    fun clearSearch(repository: SearchQueryRepository, updateUi: () -> Unit) {
        repository.deleteCache()
        viewModelScope.launch { repository.deleteAll { updateUi() } }
    }
}