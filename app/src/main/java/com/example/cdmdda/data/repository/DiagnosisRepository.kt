package com.example.cdmdda.data.repository

import com.example.cdmdda.common.Constants.DESCENDING
import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class DiagnosisRepository constructor(
    userId: String,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
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