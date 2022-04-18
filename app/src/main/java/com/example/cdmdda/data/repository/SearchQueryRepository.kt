package com.example.cdmdda.data.repository

import android.content.Context
import android.provider.SearchRecentSuggestions
import com.example.cdmdda.data.SuggestionsProvider.Companion.AUTHORITY
import com.example.cdmdda.data.SuggestionsProvider.Companion.MODE
import com.example.cdmdda.data.dto.SearchQuery
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.withContext
import java.util.*

class SearchQueryRepository constructor(
    context: Context,
    userId: String,
) : FirestoreRepository() {
    private val localQueries: SearchApi by lazy { SearchApi(context) }
    private val searchRef: CollectionReference by lazy {
        firestore.collection(ROOT).document(userId).collection(LAST)
    }

    companion object {
        const val ROOT = "search_query"
        const val LAST = "user_search_query"
        const val FIELD_QUERIED_ON = "queried_on"
        const val FIELD_QUERY      = "query"
    }

    suspend fun add(query: String, onComplete:(Boolean) -> Unit) = withContext(ioDispatcher) {
        localQueries.add(query)
        val searchQuery = SearchQuery(query, Timestamp(Date()))
        searchRef.whereEqualTo(FIELD_QUERY, query).get().addOnSuccessListener { results ->
            if (results.isEmpty) {   // CREATE new document
                searchRef.add(searchQuery)
                    .addOnCompleteListener { onComplete(it.isSuccessful) }
            } else {                // UPDATE existing document
                results.documents.first().reference.set(searchQuery, SetOptions.merge())
                    .addOnCompleteListener { onComplete(it.isSuccessful) }
            }
        }.addOnFailureListener { onComplete(false) }
    }

    suspend fun deleteAll(onTaskComplete: (Boolean) -> Unit) = withContext(ioDispatcher) {
        searchRef.get().addOnSuccessListener { collection ->
            if (collection.isEmpty) {
                onTaskComplete(true)
                return@addOnSuccessListener
            }
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