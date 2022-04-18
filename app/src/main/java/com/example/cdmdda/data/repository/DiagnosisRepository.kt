package com.example.cdmdda.data.repository

import com.example.cdmdda.common.BoolCallback
import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.withContext
import java.util.*

class DiagnosisRepository(userId: String) : FirestoreRepository() {
    private val diagnosisRef: CollectionReference by lazy {
        firestore.collection(ROOT).document(userId).collection(LAST)
    }
    val recyclerOptions by lazy { diagnosisRef.orderBy(FIELD_DIAGNOSED_ON, DESCENDING) }

    companion object {
        const val ROOT = "diagnosis"
        const val LAST = "user_diagnosis"
        const val FIELD_DIAGNOSED_ON = "diagnosed_on"
    }

    suspend fun add(diseaseId: String) = withContext(ioDispatcher) {
        val diseaseDiagnosis = DiseaseDiagnosis(diseaseId, Timestamp(Date()))
        return@withContext diagnosisRef.add(diseaseDiagnosis)
    }

    suspend fun deleteAll() = withContext(ioDispatcher) {
        diagnosisRef.get().addOnSuccessListener { collection ->
            if (collection.isEmpty) return@addOnSuccessListener
            collection.forEach { it.reference.delete() }
        }
    }

}