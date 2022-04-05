package com.example.cdmdda.domain.usecase

import android.provider.SearchRecentSuggestions
import android.util.Log
import com.example.cdmdda.common.StringFormat.capitalize
import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.presentation.SettingsActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchDiseaseUseCase(
    private val allDiseases: Array<String>,
    private val getDiseaseItemUseCase: GetDiseaseItemUseCase,
    private var queryRepository : SearchQueryRepository? = null,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {const val TAG = "SearchDiseaseUseCase"}
    sealed class SearchResult {
        data class Success(val list: List<DiseaseItem>): SearchResult()
        object Failure: SearchResult()
    }

    suspend operator fun invoke(
        query: String,
        user: FirebaseUser?,
        provider: SearchRecentSuggestions,
        onTaskComplete: (Boolean) -> Unit,
    ) : SearchResult = withContext(ioDispatcher) {
        user?.apply {
            Log.i(TAG, "line 32 reached $query")
            queryRepository = SearchQueryRepository(uid)
            saveQueryLocal(query, provider)
            queryRepository?.submit(query) {
                Log.i(TAG, "line 35 reached $query")
                onTaskComplete(it)
            }
        }
        return@withContext performSearch(query.capitalize())
    }

    private fun saveQueryLocal(query: String, provider: SearchRecentSuggestions) {
        provider.saveRecentQuery(query, null)
    }


    private suspend fun performSearch(query: String): SearchResult = withContext(defaultDispatcher) {
        val results = mutableListOf<DiseaseItem>()
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