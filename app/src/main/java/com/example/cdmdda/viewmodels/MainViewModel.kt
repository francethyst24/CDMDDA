package com.example.cdmdda.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cdmdda.R
import com.example.cdmdda.dto.Crop
import com.firebase.ui.auth.data.model.User
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var userEmail : String

    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore

    internal var options: FirestoreRecyclerOptions<Crop>
    private val cropRef = db.collection("crops")

    init {
        val query: Query = cropRef.orderBy("name")
        options = FirestoreRecyclerOptions.Builder<Crop>()
            .setQuery(query, Crop::class.java)
            .build()
    }


    fun reload() : Boolean {
        val user = auth.currentUser
        if (user != null) {
            userEmail = user.email.toString()
            return true
        }
        return false
    }


}