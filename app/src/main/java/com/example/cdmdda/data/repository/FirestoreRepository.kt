package com.example.cdmdda.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


open class FirestoreRepository(
    protected val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    protected val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        const val TAG = "FirestoreRepository"
        val DESCENDING = Query.Direction.DESCENDING
    }
}