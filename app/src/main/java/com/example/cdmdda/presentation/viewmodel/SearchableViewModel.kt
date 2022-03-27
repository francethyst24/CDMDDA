package com.example.cdmdda.presentation.viewmodel

import android.provider.SearchRecentSuggestions
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.data.dto.DiseaseItemUiState
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase.SearchResult

class SearchableViewModel(
    private val searchDiseaseUseCase: SearchDiseaseUseCase,
    private val query: String,
) : ViewModel() {
    private val _resultList: MutableList<DiseaseItemUiState> = mutableListOf()
    val resultList : List<DiseaseItemUiState> = _resultList

    fun resultCount(provider: SearchRecentSuggestions) = liveData {
        when (val result = searchDiseaseUseCase(query, provider)) {
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