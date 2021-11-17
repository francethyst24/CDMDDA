package com.example.cdmdda.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.cdmdda.dto.Crop
import com.example.cdmdda.dto.Disease
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DisplayDiseaseViewModel(application: Application, diseaseId : String) : AndroidViewModel(application) {

    private val TAG = "DisplayDiseaseViewModel"
    private var db: FirebaseFirestore = Firebase.firestore
    private val diseaseRef = db.collection("diseases").document(diseaseId)

    var disease = MutableLiveData<Disease>().apply {
        diseaseRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen to disease document failed", e)
                return@addSnapshotListener
            }
            value = if (snapshot != null && snapshot.exists()) {
                snapshot.toObject(Disease::class.java)!!
            } else Disease("crop_name", "vector")
        }
    }

}