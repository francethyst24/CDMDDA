package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.data.UserApi.user
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase

class SearchableViewModel constructor(
    private val searchDiseaseUseCase: SearchDiseaseUseCase,
    private val query: String,
) : ViewModel() {
    val uiHeadSearch by lazy { R.string.ui_head_search }

    fun resultCount(context: Context) = liveData {
        val result = searchDiseaseUseCase(context, query, user)
        emitSource(result)
    }

}