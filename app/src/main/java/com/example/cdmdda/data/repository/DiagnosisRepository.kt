package com.example.cdmdda.data.repository

import com.example.cdmdda.data.dto.DiseaseDiagnosisUiState
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import com.google.firebase.Timestamp as FirebaseTimestamp

class DiagnosisRepository(
    private val db: FirebaseFirestore,
    private val userId: String,
) {
    companion object {
        const val PATH_DIAGNOSIS = "diagnosis"
        const val FIELD_USER_ID = "user_id"
        const val FIELD_DIAGNOSED_ON = "diagnosed_on"
    }

    fun saveDiagnosis(diseaseId: String, time: Date = Date()) : Task<Void> {
        val fireTimestamp = FirebaseTimestamp(time)
        val diseaseDiagnosis = DiseaseDiagnosisUiState(diseaseId, userId, fireTimestamp)
        return db.collection(PATH_DIAGNOSIS)
            .document("$userId$fireTimestamp")
            .set(diseaseDiagnosis)
    }

    fun deleteAllDiagnosis() : Task<QuerySnapshot> {
        val userDiagnosis = db.collection(PATH_DIAGNOSIS).whereEqualTo(FIELD_USER_ID, userId)
        return userDiagnosis.get().addOnCompleteListener {
            if (it.isSuccessful && !it.result?.isEmpty!!) {
                for (document in it.result!!) {
                    db.collection(PATH_DIAGNOSIS).document(document.id).delete()
                }
            }
        }
    }

    fun getDiagnosisRecyclerOptions() : Query {
        return db.collection(PATH_DIAGNOSIS)
            .whereEqualTo(FIELD_USER_ID, userId)
            .orderBy(FIELD_DIAGNOSED_ON, Query.Direction.DESCENDING)
    }

}