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
import com.example.cdmdda.databinding.FragmentRegisterBinding
import com.google.android.material.textfield.TextInputLayout

class RegisterFragment: Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // region -- RegisterFragment interface
    private lateinit var registerFragmentListener: RegisterFragmentListener

    interface RegisterFragmentListener {
        fun onRegisterClick(email: String, password: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerFragmentListener = context as RegisterFragmentListener
    }
    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        val parentView: View? = binding.root

        // region -- UI elements
        val inputEmail: TextInputLayout = parentView!!.findViewById(R.id.til_register_email)
        val inputPassword: TextInputLayout = parentView!!.findViewById(R.id.til_register_password)
        val editEmail: EditText = inputEmail.editText!!
        val editPassword: EditText = inputPassword.editText!!
        val buttonRegister: Button = parentView!!.findViewById(R.id.button_register)

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

        // REGISTER event (validation, error, pass to activity)
        buttonRegister.setOnClickListener {
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
                    registerFragmentListener.onRegisterClick(
                        editEmail.text.toString(), editPassword.text.toString()
                    )
                }
            }
        }

        // region -- remove errors on user edit
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

    // clean up any references to the binding class instance
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}