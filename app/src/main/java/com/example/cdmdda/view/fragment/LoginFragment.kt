package com.example.cdmdda.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.example.cdmdda.R
import com.example.cdmdda.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    // region -- declare: ViewBinding
    private var binding: FragmentLoginBinding? = null
    private val layout get() = binding!!

    // endregion

    // region -- interface: FragmentListener
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
        // init: ViewBinding
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        // region -- init: UI
        val inputEmail : TextInputLayout = layout.tilLoginEmail
        val inputPassword : TextInputLayout = layout.tilLoginPassword
        val editEmail : EditText = inputEmail.editText!!
        val editPassword : EditText = inputPassword.editText!!
        // endregion

        // region -- events: UI

        // set: PasswordText.visibility
        layout.tilLoginPassword.apply {
            setEndIconOnClickListener {
                editText?.transformationMethod = if (editText?.transformationMethod != null) {
                    setEndIconDrawable(R.drawable.ic_baseline_visibility_24)
                    null
                } else {
                    setEndIconDrawable(R.drawable.ic_baseline_visibility_off_24)
                    PasswordTransformationMethod()
                }
            }
            editText?.setSelection(editText!!.text.length)
        }

        // event : Login (validation, error, pass to activity)
        layout.buttonLogin.setOnClickListener {
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

        // region -- set: ErrorText -> event: user
        layout.tilLoginPassword.apply {
            editText?.doOnTextChanged { text, start, before, count ->
                if (isErrorEnabled) error = null
                Log.d("IGNORE", "Logging params to curb warnings: $text $start $before $count")
            }
        }
        layout.tilLoginEmail.apply {
            editText?.doOnTextChanged { text, start, before, count ->
                if (isErrorEnabled) error = null
                Log.d("IGNORE", "Logging params to curb warnings: $text $start $before $count")
            }
        }
        // endregion

        // endregion

        return layout.root
    }

    // clean up any references to the binding class instance
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}