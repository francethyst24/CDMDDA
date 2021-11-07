package com.example.cdmdda.fragments

import android.content.Context
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.example.cdmdda.R
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    // region -- LoginFragment interface
    private lateinit var loginFragmentListener : LoginFragmentListener

    interface LoginFragmentListener {
        fun onLoginClick(email: String, password: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginFragmentListener = context as LoginFragmentListener
    }
    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val parentView : View? = inflater.inflate(R.layout.fragment_login, container, false)

        // region -- UI elements
        val inputEmail : TextInputLayout = parentView!!.findViewById(R.id.til_login_email)
        val inputPassword : TextInputLayout = parentView!!.findViewById(R.id.til_login_password)
        val editEmail : EditText = inputEmail.editText!!
        val editPassword : EditText = inputPassword.editText!!
        val buttonLogin : Button = parentView!!.findViewById(R.id.button_login)
        // endregion

        // region -- UI listeners

        // TOGGLE password visibility
        inputPassword.setEndIconOnClickListener {
            if (editPassword.transformationMethod != null) {
                inputPassword.setEndIconDrawable(R.drawable.ic_baseline_visibility_24)
                editPassword.transformationMethod = null
            } else {
                inputPassword.setEndIconDrawable(R.drawable.ic_baseline_visibility_off_24)
                editPassword.transformationMethod = PasswordTransformationMethod()
            }
            editPassword.setSelection(editPassword.text.length)
        }

        // LOGIN event (validation, error, pass to activity)
        buttonLogin.setOnClickListener {
            inputEmail.error = null
            inputPassword.error = null
            when {
                editEmail.text.toString().isEmpty() -> {
                    inputEmail.error = getString(R.string.fui_invalid_email_address)
                    editEmail.requestFocus()
                }
                editPassword.text.toString().isEmpty() -> {
                    inputPassword.error = getString(R.string.fui_error_invalid_password)
                    editPassword.requestFocus()
                }
                else -> {
                    loginFragmentListener.onLoginClick(
                        editEmail.text.toString(), editPassword.text.toString()
                    )
                }
            }
        }

        // region -- CLEAR errors on user edit
        editPassword.doOnTextChanged { text, start, before, count ->
            if (inputPassword.isErrorEnabled) inputPassword.error = null
        }

        editEmail.doOnTextChanged { text, start, before, count ->
            if (inputEmail.isErrorEnabled) inputEmail.error = null
        }
        // endregion

        // endregion

        return parentView
    }

}