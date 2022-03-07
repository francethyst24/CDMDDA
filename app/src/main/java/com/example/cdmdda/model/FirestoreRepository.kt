package com.example.cdmdda.model

import com.example.cdmdda.model.dto.Diagnosis
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import com.google.firebase.Timestamp as FirebaseTimestamp

class FirestoreRepository(private val DATASET: String) {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    fun saveDiagnosis(diseaseId: String, time: Date = Date()) : Task<Void> {
        val firestamp = FirebaseTimestamp(time)
        val diagnosis = Diagnosis(diseaseId, auth.uid.toString(), firestamp)
        return db.collection("diagnosis")
            .document("${auth.uid}${firestamp}")
            .set(diagnosis)
    }

    fun deleteAllDiagnosis() : Task<QuerySnapshot> {
        val userDiagnosis = db.collection("diagnosis").whereEqualTo("user_id", auth.uid)
        return userDiagnosis.get().addOnCompleteListener {
            if (it.isSuccessful && !it.result?.isEmpty!!) {
                for (document in it.result!!) {
                    db.collection("diagnosis").document(document.id).delete()
                }
            }
        }
    }

    fun getDiagnosisRecyclerOptions() : Query {
        return db.collection("diagnosis")
            .whereEqualTo("user_id", (auth.currentUser)!!.uid)
            .orderBy("diagnosed_on", Query.Direction.DESCENDING)
        /*
        return FirestoreRecyclerOptions.Builder<Diagnosis>()
            .setQuery(query, Diagnosis::class.java)
            .setLifecycleOwner(lifecycleOwner)
            .build()

         */
    }

}