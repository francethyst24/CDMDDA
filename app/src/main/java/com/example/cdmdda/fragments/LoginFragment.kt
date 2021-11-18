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
import com.example.cdmdda.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    // region -- ViewBinding decl
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // endregion

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
        // region -- ViewBinding init
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        // endregion

        // region -- UI elements
        val inputEmail : TextInputLayout = binding.tilLoginEmail
        val inputPassword : TextInputLayout = binding.tilLoginPassword
        val editEmail : EditText = inputEmail.editText!!
        val editPassword : EditText = inputPassword.editText!!
        // endregion

        // region -- UI listeners

        // TOGGLE password visibility
        binding.tilLoginPassword.apply {
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

        // LOGIN event (validation, error, pass to activity)
        binding.buttonLogin.setOnClickListener(View.OnClickListener {
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
        })

        // region -- CLEAR errors on user edit
        binding.tilLoginPassword.apply { editText?.doOnTextChanged { text, start, before, count ->
                if (isErrorEnabled) error = null
            }
        }
        binding.tilLoginEmail.apply { editText?.doOnTextChanged { text, start, before, count ->
                if (isErrorEnabled) error = null
            }
        }
        // endregion

        // endregion

        return binding.root
    }

    // clean up any references to the binding class instance
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}