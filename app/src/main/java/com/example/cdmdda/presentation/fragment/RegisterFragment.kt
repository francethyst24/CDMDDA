package com.example.cdmdda.presentation.fragment

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cdmdda.common.utils.AndroidUtils.REGISTER_REQUEST
import com.example.cdmdda.common.utils.AndroidUtils.REGISTER_RESULT
import com.example.cdmdda.common.utils.AndroidUtils.doErrorClearOnTextChanged
import com.example.cdmdda.databinding.FragmentRegisterBinding
import com.example.cdmdda.presentation.viewmodel.AccountViewModel

class RegisterFragment : Fragment() {
    // region // declare: ViewBinding, ViewModel
    private var binding: FragmentRegisterBinding? = null
    private val layout get() = binding!!
    private val viewModel: AccountViewModel by activityViewModels()
    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // init: ViewBinding
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        // set: PasswordText.visibility
        layout.tilRegisterPassword.also { textInput ->
            textInput.setEndIconOnClickListener {
                textInput.editText?.let {
                    it.setSelection(it.text.length)
                    val toggledOn = it.transformationMethod != null
                    val uiState = if (toggledOn) {
                        null to viewModel.toggledOn
                    } else PasswordTransformationMethod() to viewModel.toggledOff
                    it.transformationMethod = uiState.first
                    textInput.setEndIconDrawable(uiState.second)
                }
            }
        }

        // event: Register (validation, error, pass to activity)
        layout.buttonRegister.setOnClickListener {
            layout.tilRegisterEmail.error = null
            layout.tilRegisterPassword.error = null

            layout.tilRegisterEmail.editText?.let { view ->
                val email = view.text.toString()
                viewModel.email = if (!viewModel.validateEmail(email)) {
                    layout.tilRegisterEmail.error = getString(viewModel.noEmail)
                    view.requestFocus()
                    null
                } else email
            }

            layout.tilRegisterPassword.editText?.let { view ->
                val password = view.text.toString()
                val status = viewModel.validatePassword(password)
                viewModel.password = if (!status.first) {
                    layout.tilRegisterPassword.error = status.second?.let { getString(it) }
                    view.requestFocus()
                    null
                } else password
            }

            if (viewModel.email != null && viewModel.password != null) {
                parentFragmentManager.setFragmentResult(
                    REGISTER_REQUEST,
                    bundleOf(REGISTER_RESULT to true)
                )
            }
        }

        // clear: ErrorText -> event: user
        layout.tilRegisterPassword.doErrorClearOnTextChanged()
        layout.tilRegisterEmail.doErrorClearOnTextChanged()

        return layout.root
    }

    // clean up any references to the binding class instance
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}