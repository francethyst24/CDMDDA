package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.R
import com.example.cdmdda.common.Constants.HEALTHY
import com.example.cdmdda.common.DiagnosisRecyclerOptions
import com.example.cdmdda.data.UserApi
import com.example.cdmdda.data.dto.CropItem
import com.example.cdmdda.data.dto.UserInput
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.domain.usecase.GetCropItemUseCase
import com.example.cdmdda.domain.usecase.GetDiagnosisHistoryUseCase
import com.example.cdmdda.domain.usecase.GetDiseaseDiagnosisUseCase
import com.example.cdmdda.presentation.helper.ResourceHelper
import kotlinx.coroutines.*

class MainViewModel(
    private val toInitQueries: Boolean,
    private val getDiseaseDiagnosisUseCase: GetDiseaseDiagnosisUseCase,
    private val getDiagnosisHistoryUseCase: GetDiagnosisHistoryUseCase,
    private val getCropItemUseCase: GetCropItemUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    companion object { private const val TAG = "MainViewModel" }
    val uiTextGuest by lazy { R.string.ui_user_guest }
    val uiTextMain by lazy { R.string.ui_text_main }
    val uiDrawDividerX by lazy { R.drawable.divider_horizontal }
    val uiDrawDividerY by lazy { R.drawable.divider_vertical }
    val uiWarnLogout by lazy { R.string.ui_text_warn_logout }

    val uiMenuMain by lazy { R.menu.menu_main }
    val uiMenuItemSearch by lazy { R.id.action_search }
    val uiMenuItemOthers by lazy { listOf(R.id.action_settings, R.id.action_account) }

    val uiColrDisabledOnPrimary by lazy { R.color.material_on_primary_disabled }
    val uiColrWhite by lazy { R.color.white }

    // declare: Firebase(Auth)
    val currentUser get() = UserApi.user
    val isLoggedIn get() = UserApi.isLoggedIn
    fun signOut(context: Context) = UserApi.signOut(context)

    private val _cropList = mutableListOf<CropItem>()
    val cropList : List<CropItem> = _cropList

    fun cropCount(helper: ResourceHelper) = liveData {
        val ids = helper.allCrops
        if (cropList.size == ids.size) return@liveData
        ids.forEach { id ->
            val newCrop = getCropItemUseCase(id)
            _cropList.add(newCrop)
            _cropList.sortBy { it.name }
            emit(_cropList.indexOf(newCrop))
        }
    }

    suspend fun isUserAuthenticated(context: Context) = withContext(ioDispatcher) {
        return@withContext isLoggedIn.also {
            if (!it) return@also
            currentUser?.run {
                if (!toInitQueries) return@run
                val searchQueries = SearchQueryRepository(context, uid)
                searchQueries.deleteCache()
                searchQueries.createCache()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        getDiagnosisHistoryUseCase.onViewModelCleared()
    }

    suspend fun getFirestoreRecyclerOptions(): DiagnosisRecyclerOptions? = withContext(ioDispatcher) {
        return@withContext currentUser?.run { getDiagnosisHistoryUseCase(DiagnosisRepository(uid)) }
    }

    fun saveDiagnosis(diseaseId: String) {
        if (diseaseId == HEALTHY) return
        viewModelScope.launch(ioDispatcher) {
            getDiagnosisHistoryUseCase.add(diseaseId)
        }
    }
    // endregion

    fun startDiagnosisAsync(context: Context, input: UserInput) = viewModelScope.async {
        getDiseaseDiagnosisUseCase(context, input)
    }

    fun cancelDiagnosisAsync() = getDiseaseDiagnosisUseCase.cancelDiagnosis()

}