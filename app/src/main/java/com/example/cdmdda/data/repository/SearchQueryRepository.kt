package com.example.cdmdda.data.repository

import android.content.Context
import android.provider.SearchRecentSuggestions
import com.example.cdmdda.common.Constants.DESCENDING
import com.example.cdmdda.data.SuggestionsProvider.Companion.AUTHORITY
import com.example.cdmdda.data.SuggestionsProvider.Companion.MODE
import com.example.cdmdda.domain.model.SearchQuery
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SearchQueryRepository constructor(
    context: Context,
    userId: String,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val localQueries: SearchApi by lazy { SearchApi(context) }
    private val searchRef: CollectionReference by lazy {
        firestore.collection(ROOT).document(userId).collection(LAST)
    }

    companion object {
        const val ROOT = "search_query"
        const val LAST = "user_search_query"
        const val FIELD_QUERIED_ON = "queried_on"
        const val FIELD_QUERY = "query"
    }

    suspend fun add(query: String) = withContext(ioDispatcher) {
        localQueries.add(query)
        val searchQuery = SearchQuery(query, Timestamp(Date()))
        searchRef.whereEqualTo(FIELD_QUERY, query).get().addOnSuccessListener { results ->
            if (results.isEmpty) {   // CREATE new document
                searchRef.add(searchQuery)
            } else {                // UPDATE existing document
                results.documents.first().reference.set(searchQuery, SetOptions.merge())
            }
        }
    }

    suspend fun deleteAll() = withContext(ioDispatcher) {
        searchRef.get().addOnSuccessListener { collection ->
            if (collection.isEmpty) return@addOnSuccessListener
            collection.forEach { it.reference.delete() }
        }
    }

    suspend fun createCache() = withContext(ioDispatcher) {
        searchRef.orderBy(FIELD_QUERIED_ON, DESCENDING).get().addOnSuccessListener { collection ->
            collection.toObjects(SearchQuery::class.java)
                .reversed()
                .forEach { localQueries.add(it.query) }
        }
        return@withContext
    }

    fun deleteCache() = localQueries.deleteAll()

    inner class SearchApi(context: Context) : SearchRecentSuggestions(context, AUTHORITY, MODE) {
        fun add(queryString: String?) = super.saveRecentQuery(queryString, null)
        fun deleteAll() = super.clearHistory()
    }

}