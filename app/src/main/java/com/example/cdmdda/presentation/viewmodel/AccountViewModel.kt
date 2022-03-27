package com.example.cdmdda.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel

class AccountViewModel: ViewModel() {
    var email : String? = null
    var password : String? = null
    lateinit var passwordStatus : Pair<Boolean, String?>

    fun validateEmail(email: String) = email.trim().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun validatePassword(password: String) {
        passwordStatus = when {
            !password.contains(Regex("[A-Z]")) -> {
                false to "Password must contain one capital letter"
            }
            !password.contains(Regex("[0-9]")) -> {
                false to "Password must contain one digit"
            }
            !password.contains(Regex("[^a-zA-Z0-9 ]")) -> {
                false to "Password must contain one special character"
            }
            else -> {
                true to null
            }
        }
    }
}