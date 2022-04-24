package com.example.cdmdda.presentation.fragment

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cdmdda.common.AndroidUtils.LOGIN_REQUEST
import com.example.cdmdda.common.AndroidUtils.LOGIN_RESULT
import com.example.cdmdda.common.AndroidUtils.doErrorClearOnTextChanged
import com.example.cdmdda.databinding.FragmentLoginBinding
import com.example.cdmdda.presentation.viewmodel.AccountViewModel

class LoginFragment : Fragment() {

    // region // declare: ViewBinding, ViewModel
    private var binding: FragmentLoginBinding? = null
    private val layout get() = binding!!
    private val model: AccountViewModel by activityViewModels()
    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // init: ViewBinding
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        // set: PasswordText.visibility
        layout.tilLoginPassword.also { textInput ->
            textInput.setEndIconOnClickListener {
                textInput.editText?.let {
                    it.setSelection(it.text.length)
                    val toggledOn = it.transformationMethod != null
                    val uiState = if (toggledOn) {
                        null to model.toggledOn
                    } else PasswordTransformationMethod() to model.toggledOff
                    it.transformationMethod = uiState.first
                    textInput.setEndIconDrawable(uiState.second)
                }
            }
        }

        // event : Login (validation, error, pass to activity)
        layout.buttonLogin.setOnClickListener {
            layout.tilLoginEmail.error = null
            layout.tilLoginPassword.error = null

            layout.tilLoginEmail.editText?.let { view ->
                val email = view.text.toString()
                model.email = if (!model.validateEmail(email)) {
                    layout.tilLoginEmail.error = getString(model.noEmail)
                    view.requestFocus()
                    null
                } else email
            }

            layout.tilLoginPassword.editText?.let { view ->
                val password = view.text.toString()
                val status = model.validatePassword(password)
                model.password = if (!status.first) {
                    layout.tilLoginPassword.error = status.second?.let { getString(it) }
                    view.requestFocus()
                    null
                } else password
            }

            if (model.email != null && model.password != null) {
                parentFragmentManager.setFragmentResult(
                    LOGIN_REQUEST,
                    bundleOf(LOGIN_RESULT to true)
                )
            }
        }

        // clear: ErrorText -> event: user
        layout.tilLoginPassword.doErrorClearOnTextChanged()
        layout.tilLoginEmail.doErrorClearOnTextChanged()

        return layout.root
    }

    // clean up any references to the binding class instance
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}