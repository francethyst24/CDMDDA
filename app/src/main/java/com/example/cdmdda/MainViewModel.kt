package com.example.cdmdda

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.cdmdda.dto.Crop
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    var userEmail : String? = null

    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore

    var options: FirestoreRecyclerOptions<Crop>
    private val cropRef : CollectionReference = db.collection("crops")

    init {
        val query: Query = cropRef.orderBy("name")
        options = FirestoreRecyclerOptions.Builder<Crop>()
            .setQuery(query, Crop::class.java)
            .build()
    }


    fun reload() : Boolean {
        val user = auth.currentUser
        if (user != null) {
            userEmail = user.email
            return true
        }
        return false
    }


}