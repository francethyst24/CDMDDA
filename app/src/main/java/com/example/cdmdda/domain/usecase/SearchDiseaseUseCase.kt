package com.example.cdmdda.domain.usecase

import android.content.Context
import androidx.lifecycle.liveData
import com.example.cdmdda.common.StringUtils.capitalize
import com.example.cdmdda.common.StringUtils.wordAt
import com.example.cdmdda.common.StringUtils.wordIndices
import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.data.repository.DiseaseRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase.SearchResult.Failure
import com.example.cdmdda.domain.usecase.SearchDiseaseUseCase.SearchResult.Success
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchDiseaseUseCase(
    private val allDiseases: Array<String>,
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

    suspend operator fun invoke(context: Context, query: String, user: FirebaseUser?) =
        withContext(ioDispatcher) {
            if (user != null) {
                val queryRepository = SearchQueryRepository(context, user.uid)
                queryRepository.add(query)
            }
            return@withContext performSearch(context, query.capitalize())
        }

    private fun performSearch(context: Context, query: String) = liveData(defaultDispatcher) {
        val results = mutableListOf<DiseaseItem>()
        allDiseases.forEach { id ->
            id.wordIndices.forEach {
                if (id.wordAt(it).startsWith(query)) {
                    val repository = DiseaseRepository(context, id)
                    results.add(repository.getItem())
                }
            }
        }
        val emitValue = with (results) {
            if (isEmpty()) Failure else Success(sortedBy { it.id })
        }
        emit(emitValue)
    }

}