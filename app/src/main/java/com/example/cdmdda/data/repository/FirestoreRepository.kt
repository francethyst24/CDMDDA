package com.example.cdmdda.data.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


open class FirestoreRepository(
    protected val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        const val TAG = "FirestoreRepository"
        val DESCENDING = Query.Direction.DESCENDING
    }

}