package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.R
import com.example.cdmdda.common.AndroidUtils.getStringArray
import com.example.cdmdda.common.Constants.ALL_CROPS
import com.example.cdmdda.common.Constants.FAILED_VALUES
import com.example.cdmdda.common.DiagnosisUiState
import com.example.cdmdda.common.StringUtils.equalsAny
import com.example.cdmdda.data.UserApi
import com.example.cdmdda.data.dto.CropItem
import com.example.cdmdda.data.repository.CropRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.domain.model.Diagnosable
import com.example.cdmdda.domain.usecase.GetDiagnosisHistoryUseCase
import com.example.cdmdda.domain.usecase.GetDiseaseDiagnosisUseCase
import com.example.cdmdda.presentation.fragment.LogoutDialog
import com.example.cdmdda.presentation.viewmodel.factory.LogoutViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val toInitQueries: Boolean,
    private val getDiagnosisHistoryUseCase: GetDiagnosisHistoryUseCase,
    private val getDiseaseDiagnosisUseCase: GetDiseaseDiagnosisUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LogoutViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }

    // region // ResourceIds
    val uiTextGuest by lazy { R.string.ui_user_guest }
    val uiTextMain by lazy { R.string.ui_text_main }
    val uiDrawDividerX by lazy { R.drawable.divider_horizontal }
    val uiDrawDividerY by lazy { R.drawable.divider_vertical }

    val uiMenuMain by lazy { R.menu.menu_main }
    val uiMenuItemSearch by lazy { R.id.action_search }
    val uiMenuItemSettings by lazy { R.id.action_settings }
    val uiMenuItemAccount by lazy { R.id.action_account }
    val uiMenuItemOthers by lazy { listOf(uiMenuItemSettings, uiMenuItemAccount) }
    val uiMenuSearchEditText by lazy { R.id.search_src_text }

    val uiColrDisabledOnPrimary by lazy { R.color.material_on_primary_disabled }
    val uiColrWhite by lazy { R.color.white }

    // VerifyEmailFragment
    val uiDescVerifyEmail by lazy { R.string.ui_desc_verify_email }
    val uiHeadVerifyEmail by lazy { R.string.ui_head_verify_email }

    // DiagnosisFailFragment
    val uiDescDiagnosisFail by lazy { R.string.ui_desc_diagnosis_fail }
    val uiTextLearnMore by lazy { R.string.ui_text_learn_more }
    val uiTextDisease by lazy { R.string.ui_text_disease }
    val uiTextLeaf by lazy { R.string.ui_text_leaf }

    val uiTextOk by lazy { android.R.string.ok }
    // endregion

    // declare: Firebase(Auth)
    val currentUser get() = UserApi.user
    val isLoggedIn  get() = UserApi.isLoggedIn

    // nullable vars
    var diagnosableInput: Diagnosable? = null
    private var options : FirestoreRecyclerOptions<DiagnosisUiState>? = null

    var showOnStart: Boolean = true

    private val _isEmailVerified = MutableLiveData(false)
    val isEmailVerified: LiveData<Boolean> = _isEmailVerified
    private val _cropList = mutableListOf<CropItem>()

    val cropList: List<CropItem> = _cropList

    fun reloadUser() = currentUser?.run {
        reload().addOnSuccessListener {
            _isEmailVerified.value = isEmailVerified
        }
    }

    fun cropCount(context: Context) = liveData {
        val ids = context.getStringArray(ALL_CROPS)
        if (cropList.size == ids.size) return@liveData
        ids.forEach { id ->
            val newCrop = CropRepository(context, id).getItem()
            _cropList.add(newCrop)
            _cropList.sortBy { context.getString(it.name) }
            emit(_cropList.indexOf(newCrop))
        }
    }

    fun initSearchQueries(context: Context) = viewModelScope.launch {
        currentUser?.let {
            if (!toInitQueries) return@let
            val searchQueries = SearchQueryRepository(context, it.uid)
            searchQueries.deleteCache()
            searchQueries.createCache()
        }
    }

    // region // DiagnosisHistory
    override fun onCleared() {
        options?.let { getDiagnosisHistoryUseCase.onViewModelCleared() }
        super.onCleared()
    }

    suspend fun getDiagnosisOptions(): FirestoreRecyclerOptions<DiagnosisUiState>? {
        options = currentUser?.let { getDiagnosisHistoryUseCase(it.uid) }
        return options
    }
    // endregion

    // region // Diagnosis

    fun getDiagnosis(context: Context, input: Diagnosable) = liveData {
        val result = getDiseaseDiagnosisUseCase(context, input)
        if (isLoggedIn && !result.equalsAny(FAILED_VALUES)) saveDiagnosis(result)
        emit(result)
    }

    fun cancelDiagnosisAsync() = getDiseaseDiagnosisUseCase.cancelDiagnosis()

    private fun saveDiagnosis(diseaseId: String) = viewModelScope.launch(ioDispatcher) {
        getDiagnosisHistoryUseCase.add(diseaseId)
    }
    // endregion

}