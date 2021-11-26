package com.example.cdmdda.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchableViewModel(application: Application) : AndroidViewModel(application) {
    fun doMySearch(query: String) {

    }

    private val TAG = "SearchableViewModel"

    private var db: FirebaseFirestore = Firebase.firestore
    private val diseaseRef = db.collection("disease_sets")
        .document("set1").collection("diseases")


}