package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.data.UserApi
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase.SearchResult.Failure
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase.SearchResult.Success
import com.example.cdmdda.presentation.SettingsActivity.Companion.TAG

class SearchableViewModel(
    private val searchDiseaseUseCase: SearchDiseaseUseCase,
    private val query: String,
) : ViewModel() {
    val uiHeadSearch by lazy { R.string.ui_head_search }

    fun resultCount(context: Context) = liveData {
        val result = searchDiseaseUseCase(context, query, UserApi.user) {
            if (it.not()) Log.e(TAG, "Error adding/updating Firestore document")
        }
        when (result) {
            is Success -> emit(result.list)
            is Failure -> emit(null)
        }
    }

}