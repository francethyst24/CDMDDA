package com.example.cdmdda.domain.usecase

import android.provider.SearchRecentSuggestions
import com.example.cdmdda.common.StringFormat.capitalize
import com.example.cdmdda.data.dto.DiseaseItemUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchDiseaseUseCase(
    private val allDiseases: Array<String>,
    private val getDiseaseItemUseCase: GetDiseaseItemUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    sealed class SearchResult {
        data class Success(val list: List<DiseaseItemUiState>): SearchResult()
        object Failure: SearchResult()
    }

    suspend operator fun invoke(
        query: String,
        provider: SearchRecentSuggestions
    ) : SearchResult = withContext(ioDispatcher) {
        saveQuery(query, provider)
        return@withContext performSearch(query.capitalize())
    }

    private fun saveQuery(query: String, provider: SearchRecentSuggestions) {
        provider.saveRecentQuery(query, null)
    }

    private suspend fun performSearch(query: String): SearchResult = withContext(defaultDispatcher) {
        //val allDiseases = globals.allDiseases()
        val results = mutableListOf<DiseaseItemUiState>()
        for (disease in allDiseases) {
            val wordCount = disease.getWordCount()
            for (i in 0..wordCount) {
                val word = disease.getWordAt(i)
                if (word.startsWith(query)) {
                    val diseaseItemUiState = getDiseaseItemUseCase(disease)
                    results.add(diseaseItemUiState)
                    withContext(defaultDispatcher) { results.sortBy { it.id } }
                }
            }
        }
        if (results.isEmpty()) SearchResult.Failure else SearchResult.Success(results)
    }

    private suspend fun String.getWordCount() = withContext(defaultDispatcher) {
        count { it == " ".single() }
    }

    private suspend fun String.getWordAt(index: Int) = withContext(defaultDispatcher) {
        split(Regex(" "), index + 1).last()
    }

}