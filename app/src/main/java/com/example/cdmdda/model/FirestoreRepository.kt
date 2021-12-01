package com.example.cdmdda.model

import com.example.cdmdda.model.dto.Crop
import com.example.cdmdda.model.dto.Diagnosis
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

class FirestoreRepository(private val DATASET: String) {
    private var db = Firebase.firestore
    private var auth = Firebase.auth

    fun saveDiagnosis(diseaseId: String) : Task<Void> {
        val diagnosis = hashMapOf(
            "name" to diseaseId,
            "user_id" to (auth.currentUser)!!.uid,
            "diagnosed_on" to Timestamp(System.currentTimeMillis())
        )
        return db.collection("diagnosis").document(diseaseId).set(diagnosis)
    }

    fun getCropRecyclerOptions() : FirestoreRecyclerOptions<Crop> {
        val query = db.collection("crop_sets")
            .document(DATASET)
            .collection("crops")
            .orderBy("name")
        return FirestoreRecyclerOptions.Builder<Crop>()
            .setQuery(query, Crop::class.java)
            .build()
    }

    fun getDiagnosisRecyclerOptions() : FirestoreRecyclerOptions<Diagnosis> {
        val query = db.collection("diagnosis")
            .whereEqualTo("user_id", (auth.currentUser)!!.uid)
            .orderBy("diagnosed_on", Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<Diagnosis>()
            .setQuery(query, Diagnosis::class.java)
            .build()
    }

    /*
    fun getSupportedDiseases() : CollectionReference {
        return db.collection("disease_sets")
            .document(DATASET)
            .collection("diseases")
    }
     */

}