package com.example.cdmdda.data

import android.content.Context
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object UserApi {
    private val auth by lazy { Firebase.auth }
    val user: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = user != null
    fun signOut(context: Context) {
        user?.let { SearchQueryRepository(context, it.uid).deleteCache() }
        auth.signOut()
    }

    fun register(email: String, password: String) = auth.createUserWithEmailAndPassword(email, password)
    fun login(email: String, password: String) = auth.signInWithEmailAndPassword(email, password)
    fun verify() = auth.currentUser?.sendEmailVerification()
}