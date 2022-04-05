package com.example.cdmdda.data.repository

import android.provider.SearchRecentSuggestions
import android.util.Log
import com.example.cdmdda.data.dto.SearchQuery
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SearchQueryRepository(
    userId : String,
    private val db: CollectionReference = Firebase.firestore
        .collection(ROOT)
        .document(userId)
        .collection(LAST),
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) : FirestoreRepository() {

    companion object {
        const val ROOT = "search_query"
        const val LAST = "user_search_query"
        const val FIELD_QUERIED_ON = "queried_on"
        const val FIELD_QUERY      = "query"
    }

    private val searchHistory get() = db.orderBy(FIELD_QUERIED_ON, DESCENDING).get()

    suspend fun submit(query: String, onComplete:(Boolean) -> Unit) = withContext(ioDispatcher) {
        val searchQuery = SearchQuery(query, Timestamp(Date()))
        Log.i("SearchQueryRepository", "line 37: beforeAsync db.add($query)")
        db.whereEqualTo(FIELD_QUERY, query).limit(1).get().addOnSuccessListener { result ->
            Log.i("SearchQueryRepository", "line 39: insideAsync db.add($query)")
            if (result.isEmpty) {   // CREATE new document
                Log.i("SearchQueryRepository", "line 41: result.isEmpty db.add($query)")
                db.add(searchQuery).addOnCompleteListener {
                    onComplete(it.isSuccessful)
                }
                return@addOnSuccessListener
            }
            result.forEach { doc ->
                if (doc.exists()) { // UPDATE existing document
                    val options = SetOptions.merge()
                    db.document(doc.id).set(searchQuery, options).addOnCompleteListener {
                        onComplete(it.isSuccessful)
                    }
                }
            }
        }.addOnFailureListener { onComplete(false) }

        /*updateTimestamp(query) { updatedExisting ->
            if (updatedExisting) return@updateTimestamp
            val searchQuery = SearchQuery(query, Timestamp(Date()))
            db.add(searchQuery).addOnCompleteListener { onTaskComplete(it.isSuccessful) }
        }*/
    }

    suspend fun deleteAll(onTaskComplete: (Boolean) -> Unit) = withContext(ioDispatcher) {
        db.get().addOnSuccessListener { result ->
            onTaskComplete(true)
            if (result.isEmpty) return@addOnSuccessListener
            result.forEach { db.document(it.id).delete() }
        }
    }

    suspend fun fetchOnline(provider: SearchRecentSuggestions) = withContext(mainDispatcher) {
        searchHistory.addOnSuccessListener { result ->
            result.forEach {
                val entry = it.toObject<SearchQuery>().query
                provider.saveRecentQuery(entry, null)
            }
        }
    }

    fun deleteCache(provider: SearchRecentSuggestions) = provider.clearHistory()

}