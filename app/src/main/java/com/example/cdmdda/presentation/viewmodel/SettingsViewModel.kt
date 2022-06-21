package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.R
import com.example.cdmdda.data.UserApi
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SettingsViewModel(
    defaultLocale: String,
) : ViewModel() {
    companion object {
        const val DEFAULT_THEME = "default"
        const val DEFAULT_LOCALE = "en"
        const val PREF_THEME = "theme"
        const val PREF_LOCAL = "lang"
        const val PREF_PERSONAL = "personal"
        const val PREF_CLEAR_DIAGNOSIS = "clear_diagnosis"
        const val PREF_CLEAR_SEARCH_QUERY = "clear_search"
        const val PREF_LOGOUT = "logout"
        const val PREF_LEARN_MORE = "learn_more"
    }

    // region // ResourceIds
    // Activity
    val uiSettings by lazy { R.id.settings }
    val uiInfoClearDiagnosisSuccess by lazy { R.string.ui_text_clear_diagnosis_success }
    val uiInfoClearSearchSuccess by lazy { R.string.ui_text_clear_search_success }
    val uiHeadSettings by lazy { R.string.ui_text_settings }


    // SettingsFragment
    val xmlRoot by lazy { R.xml.root_preferences }

    // ClearXDialog
    val uiWarnClearSearch by lazy { R.string.ui_warn_clear_search }
    val uiTextClearSearch by lazy { R.string.ui_text_clear_search }
    val uiWarnClearDiagnosis by lazy { R.string.ui_warn_clear_diagnosis }
    val uiTextClearDiagnosis by lazy { R.string.ui_text_clear_diagnosis }
    val uiTextOk by lazy { android.R.string.ok }
    val uiTextCancel by lazy { R.string.ui_text_cancel }
    // endregion

    val user: FirebaseUser? get() = UserApi.user
    val isLoggedIn: Boolean get() = UserApi.isLoggedIn

    private val _isClearDiagnosisConfirmed = MutableLiveData<String?>(null)
    val isClearDiagnosisConfirmed: LiveData<String?> = _isClearDiagnosisConfirmed
    fun confirmClearDiagnosis(uid: String?) {
        _isClearDiagnosisConfirmed.value = uid
    }

    private val _isClearSearchConfirmed = MutableLiveData<String?>(null)
    val isClearSearchConfirmed: LiveData<String?> = _isClearSearchConfirmed
    fun confirmClearSearch(uid: String?) {
        _isClearSearchConfirmed.value = uid
    }

    private val _isLocalChangeConfirmed = MutableLiveData(defaultLocale)
    val isLocalChangeConfirmed: LiveData<String> = _isLocalChangeConfirmed
    fun confirmLocalChange(newVal: String) {
        _isLocalChangeConfirmed.value = newVal
    }


    fun clearDiagnosis(uid: String) {
        val repository = DiagnosisRepository(uid)
        viewModelScope.launch { repository.deleteAll() }
    }

    fun clearSearch(context: Context, uid: String) {
        val repository = SearchQueryRepository(context, uid)
        repository.deleteCache()
        viewModelScope.launch { repository.deleteAll() }
    }

}