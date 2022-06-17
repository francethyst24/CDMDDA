package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.R
import com.example.cdmdda.common.Constants.ALL_CROPS
import com.example.cdmdda.common.Constants.FAILED_VALUES
import com.example.cdmdda.common.DiagnosisUiState
import com.example.cdmdda.common.utils.AndroidUtils.getStringArray
import com.example.cdmdda.common.utils.StringUtils.equalsAny
import com.example.cdmdda.data.UserApi
import com.example.cdmdda.data.dto.CropItem
import com.example.cdmdda.data.repository.CropRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.domain.model.Diagnosable
import com.example.cdmdda.domain.usecase.GetDiagnosisHistoryUseCase
import com.example.cdmdda.domain.usecase.GetDiseaseDiagnosisUseCase
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
    val isLoggedIn get() = UserApi.isLoggedIn

    private val _userDiagnosableState = MutableLiveData<Diagnosable?>(null)
    val userDiagnosableState: LiveData<Diagnosable?> = _userDiagnosableState
    fun submitDiagnosable(diagnosable: Diagnosable) {
        _userDiagnosableState.value = diagnosable
    }

    fun finishDiagnosableSubmission() {
        _userDiagnosableState.value = null
    }

    private val _diagnosisResultState = MutableLiveData<Pair<String, Float>?>(null)
    val diagnosisResultState: LiveData<Pair<String, Float>?> = _diagnosisResultState
    fun clearDiagnosisResult() {
        _diagnosisResultState.value = null
    }


    // nullable vars
    /*var diagnosableInput: Diagnosable? = null*/
    private var options: FirestoreRecyclerOptions<DiagnosisUiState>? = null

    private val _verifyEmailDialogUiState = MutableLiveData(true)
    val verifyEmailDialogUiState: LiveData<Boolean> = _verifyEmailDialogUiState
    fun finishedShowingVerifyEmailDialog() {
        _verifyEmailDialogUiState.value = false
    }

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

    private val _isEmailVerified = MutableLiveData(false)
    val isEmailVerified: LiveData<Boolean> = _isEmailVerified
    private val _cropList = mutableListOf<CropItem>()

    private var hasFetchedCropStates = false
    val cropList: List<CropItem> = _cropList

    fun reloadUser() = currentUser?.run {
        reload().addOnSuccessListener {
            _isEmailVerified.value = isEmailVerified
        }
    }

    fun cropCount(context: Context) = liveData {
        val ids = context.getStringArray(ALL_CROPS)
        if (hasFetchedCropStates) return@liveData
        ids.forEach { id ->
            val newCrop = CropRepository(context, id).getItem()
            _cropList.add(newCrop)
            //_cropList.sortBy { context.getString(it.name) }
            _cropList.sortByDescending { it.isDiagnosable }
            emit(_cropList.indexOf(newCrop))
        }
        hasFetchedCropStates = true
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
        options = currentUser?.let { getDiagnosisHistoryUseCase(it.uid, true) }
        return options
    }
    // endregion

    // region // Diagnosis

    fun launchDiagnosis(context: Context, input: Diagnosable) = viewModelScope.launch {
        val result = getDiseaseDiagnosisUseCase(context, input)
        _diagnosisResultState.value = result
        if (isLoggedIn && !result.first.equalsAny(FAILED_VALUES)) commitDiagnosis(result.first)
        /*emit(result)*/
    }

    fun cancelDiagnosis() = getDiseaseDiagnosisUseCase.cancelDiagnosis()

    private fun commitDiagnosis(diseaseId: String) = viewModelScope.launch(ioDispatcher) {
        getDiagnosisHistoryUseCase.add(diseaseId)
    }
    // endregion

}