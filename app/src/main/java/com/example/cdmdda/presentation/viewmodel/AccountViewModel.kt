package com.example.cdmdda.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.cdmdda.R
import com.example.cdmdda.common.Constants.CAPITAL
import com.example.cdmdda.common.Constants.NUMERAL
import com.example.cdmdda.data.UserApi
import java.util.regex.Pattern

class AccountViewModel constructor(
    private val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
) : ViewModel() {

    // Activity
    val uiTextLogin by lazy { R.string.ui_text_login }
    val uiTextRegister by lazy { R.string.ui_text_register }
    val uiDescSentEmail by lazy { R.string.ui_desc_sent_verify_email }
    val uiWarnRegister by lazy { R.string.fui_email_account_creation_error }
    val uiWarnLogin by lazy { R.string.fui_error_unknown }

    // Fragments
    val toggledOff by lazy { R.drawable.ic_baseline_visibility_off_24 }
    val toggledOn by lazy { R.drawable.ic_baseline_visibility_24 }
    val noEmail by lazy { R.string.fui_invalid_email_address }
    private val noCapital by lazy { R.string.ui_warn_password_fail_capital }
    private val noNumeral by lazy { R.string.ui_warn_password_fail_numeral }
    //private val noSpecial by lazy { R.string.ui_warn_password_fail_special }

    var email: String? = null
    var password: String? = null

    fun validateEmail(email: String) = email.trim().isNotEmpty() && emailPattern.matcher(email).matches()

    fun validatePassword(password: String) = when {
        !password.contains(Regex(CAPITAL)) -> false to noCapital
        !password.contains(Regex(NUMERAL)) -> false to noNumeral
        //!password.contains(Regex(SPECIAL)) -> false to noSpecial
        else -> true to null
    }

    private val credentials get() = (email to password)
    fun registerUser() = credentials.let { email, password -> UserApi.register(email, password) }

    fun loginUser() = credentials.let { email, password -> UserApi.login(email, password) }

    fun verifyUser() = UserApi.verify()

    private fun <I1, I2, O> Pair<I1?, I2?>.let(lambda: (I1, I2) -> O): O? {
        return if (first != null && second != null) {
            @Suppress("UNCHECKED_CAST")
            lambda(first as I1, second as I2)
        } else null
    }

}