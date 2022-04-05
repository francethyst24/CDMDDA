package com.example.cdmdda.presentation.viewmodel

import android.provider.SearchRecentSuggestions
import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.domain.usecase.GetAuthStateUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountViewModel: ViewModel() {
    var email : String? = null
    var password : String? = null

    fun validateEmail(email: String) = email.trim().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun validatePassword(pw: String) = when {
        !pw.contains(Regex("[A-Z]")) -> false to "Password must contain one capital letter"
        !pw.contains(Regex("[0-9]")) -> false to "Password must contain one digit"
        //!pw.contains(Regex("[^a-zA-Z0-9 ]")) -> false to "Password must contain one special character"
        else -> true to null
    }

}