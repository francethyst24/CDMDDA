package com.example.cdmdda.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cdmdda.R
import com.example.cdmdda.model.FirestoreRepository
import com.example.cdmdda.model.dto.Disease
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DisplayDiseaseViewModel(application: Application, diseaseId : String) : AndroidViewModel(application) {
    companion object { private const val TAG = "DisplayDiseaseViewModel" }
    private val repository = FirestoreRepository(application.getString(R.string.dataset))

    private var _disease = MutableLiveData<Disease>().apply {
        repository.getDisease(diseaseId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen to disease document failed", e)
                return@addSnapshotListener
            }
            value = if (snapshot != null && snapshot.exists()) {
                snapshot.toObject(Disease::class.java)!!
            } else Disease("disease_name", "vector")
        }
    }
    val disease: LiveData<Disease> get() = _disease

}