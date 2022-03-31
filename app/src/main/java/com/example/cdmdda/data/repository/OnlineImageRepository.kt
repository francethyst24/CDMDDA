package com.example.cdmdda.data.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OnlineImageRepository(
    private val set: String,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend fun fetchOnlineImages(diseaseId: String, onImageReceived: (Uri) -> Unit) = withContext(ioDispatcher) {
        val diseaseRef = Firebase.storage.reference.child("disease_sets/$set/$diseaseId")
        diseaseRef.listAll().addOnSuccessListener {
            if (it == null) return@addOnSuccessListener
            for (item in it.items) {
                item.downloadUrl.addOnSuccessListener { uri ->
                    onImageReceived(uri)
                }
            }
        }
    }

}