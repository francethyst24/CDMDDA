package com.example.cdmdda.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.cdmdda.dto.Crop
import com.example.cdmdda.dto.Diagnosis
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "MainViewModel"

    // region -- declare: Firebase - Auth, Firestore
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore
    // endregion

    // region -- init: Collection
    private val cropRef = db.collection("crops")
    private val diseaseRef = db.collection("diseases")
    private val countRef = db.collection("counters").document("diseases")

    // endregion

    lateinit var preview: Bitmap

    var user: FirebaseUser? = if (auth.currentUser != null) { auth.currentUser } else { null }
    var mainRecyclerOptions = FirestoreRecyclerOptions.Builder<Crop>()
        .setQuery(cropRef.orderBy("name"), Crop::class.java)
        .build()


    private val diagnosisRef = db.collection("diagnosis")
    private lateinit var diagnosisQuery: Query
    lateinit var diagnosisRecyclerOptions : FirestoreRecyclerOptions<Diagnosis>


    var diseaseList = ArrayList<String>().apply {
        diseaseRef.get().addOnCompleteListener {
            if (it.isSuccessful) for (document in it.result!!) this.add(document.id)
        }
    }

    var classCount : Long = 0
    init {
        countRef.addSnapshotListener{ snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen to disease document failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                classCount = snapshot.get("count") as Long
            }
        }
    }


    fun reload() : Boolean {
        return (auth.currentUser != null).also {
            if (auth.currentUser != null) {
                user = auth.currentUser!!
                diagnosisQuery = diagnosisRef.whereEqualTo("user_id", user!!.uid)
                    .orderBy("diagnosed_on", Query.Direction.DESCENDING)

                diagnosisRecyclerOptions = FirestoreRecyclerOptions.Builder<Diagnosis>()
                    .setQuery(diagnosisQuery, Diagnosis::class.java)
                    .build()
            }
        }
    }

    fun savePreview(bitmap: Bitmap) : Bitmap {
        preview = bitmap
        return preview
    }

    fun addDiagnosis(diseaseId: String) {
        val diagnosis = hashMapOf(
            "name" to diseaseId,
            "user_id" to auth.uid,
            "diagnosed_on" to Timestamp(System.currentTimeMillis())
        )

        diagnosisRef.document(diseaseId).set(diagnosis)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }

}