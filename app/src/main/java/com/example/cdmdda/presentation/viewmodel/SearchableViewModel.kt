package com.example.cdmdda.presentation.viewmodel

import android.provider.SearchRecentSuggestions
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.domain.usecase.GetAuthStateUseCase
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase.SearchResult
import com.example.cdmdda.presentation.SettingsActivity.Companion.TAG

class SearchableViewModel(
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val searchDiseaseUseCase: SearchDiseaseUseCase,
    private val query: String,
) : ViewModel() {
    private val _resultList: MutableList<DiseaseItem> = mutableListOf()
    val resultList : List<DiseaseItem> = _resultList

    fun resultCount(provider: SearchRecentSuggestions) = liveData {
        val result = searchDiseaseUseCase(query, getAuthStateUseCase(), provider) {
            if (!it) Log.w(TAG, "Error adding/updating Firestore document")
        }
        when (result) {
            is SearchResult.Success -> {
                _resultList.clear()
                _resultList.addAll(result.list)
                _resultList.sortBy { it.id }
                emit(result.list.size-1)
            }
            is SearchResult.Failure -> emit(-1)
        }
    }

}