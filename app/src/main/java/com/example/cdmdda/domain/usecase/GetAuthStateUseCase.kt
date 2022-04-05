package com.example.cdmdda.domain.usecase

import android.provider.SearchRecentSuggestions
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GetAuthStateUseCase(
    private val auth: FirebaseAuth = Firebase.auth,
) {
    operator fun invoke(): FirebaseUser? = auth.currentUser
    fun signOut(provider: SearchRecentSuggestions) {
        GlobalScope.launch {
            invoke()?.let {
                val repository = SearchQueryRepository(it.uid)
                repository.deleteCache(provider)
            }
        }
        auth.signOut()
    }
}