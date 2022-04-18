package com.example.cdmdda.domain.usecase

import android.content.Context
import com.example.cdmdda.common.BoolCallback
import com.example.cdmdda.common.StringUtils.capitalize
import com.example.cdmdda.common.StringUtils.wordAt
import com.example.cdmdda.common.StringUtils.wordIndices
import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase.SearchResult.Failure
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase.SearchResult.Success
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchDiseaseUseCase(
    private val allDiseases: Array<String>,
    private val getDiseaseItemUseCase: GetDiseaseItemUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        const val TAG = "SearchDiseaseUseCase"
    }

    sealed class SearchResult {
        data class Success(val list: List<DiseaseItem>) : SearchResult()
        object Failure : SearchResult()
    }

    suspend operator fun invoke(context: Context, query: String, user: FirebaseUser?, onTaskComplete: BoolCallback) =
        withContext(ioDispatcher) {
            user?.apply {
                val queryRepository = SearchQueryRepository(context, uid)
                queryRepository.add(query) { onTaskComplete(it) }
            }
            return@withContext performSearch(query.capitalize())
        }

    private suspend fun performSearch(query: String) = withContext(defaultDispatcher) {
        val results = mutableListOf<DiseaseItem>()
        for (disease in allDiseases) {
            for (index in disease.wordIndices) {
                if (disease.wordAt(index).startsWith(query)) {
                    results.add(getDiseaseItemUseCase(disease))
                    results.sortBy { it.id }
                }
            }
        }
        if (results.isEmpty()) Failure else Success(results)
    }

}