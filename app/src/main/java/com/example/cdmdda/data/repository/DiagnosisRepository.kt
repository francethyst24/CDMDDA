package com.example.cdmdda.data.repository

import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.withContext
import java.util.*

class DiagnosisRepository(
    userId: String,
    private val db: CollectionReference = Firebase.firestore
        .collection(ROOT)
        .document(userId)
        .collection(LAST),
) : FirestoreRepository() {
    companion object {
        const val ROOT = "diagnosis"
        const val LAST = "user_diagnosis"
        const val FIELD_DIAGNOSED_ON = "diagnosed_on"
    }

    suspend fun add(diseaseId: String, now: Date = Date()) = withContext(ioDispatcher) {
        val diseaseDiagnosis = DiseaseDiagnosis(diseaseId, Timestamp(now))
        return@withContext db.add(diseaseDiagnosis)
    }

    suspend fun deleteAll(onTaskComplete: (Boolean) -> Unit) = withContext(ioDispatcher) {
        /*val userDiagnosis = db.document(userId).collection(CHILD)
        return userDiagnosis*/db.get().addOnSuccessListener { result ->
            onTaskComplete(true)
            if (result.isEmpty) return@addOnSuccessListener
            result.forEach { db.document(it.id).delete() }
        }
    }

    val recyclerOptions get() = db.orderBy(FIELD_DIAGNOSED_ON, DESCENDING)

}