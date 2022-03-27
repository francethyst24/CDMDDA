package com.example.cdmdda.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cdmdda.R
import com.example.cdmdda.databinding.FragmentRegisterBinding
import com.example.cdmdda.presentation.utils.toggleTextVisibility
import com.example.cdmdda.presentation.viewmodel.AccountViewModel

class RegisterFragment(private val onPositiveButtonClick: () -> Unit): Fragment() {
    // region // declare: ViewBinding, ViewModel
    private var binding: FragmentRegisterBinding? = null
    private val layout get() = binding!!
    private val model: AccountViewModel by activityViewModels()
    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // init: ViewBinding
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        // set: PasswordText.visibility
        layout.tilRegisterPassword.also { textInput ->
            textInput.setEndIconOnClickListener {
                textInput.editText?.let {
                    textInput.toggleTextVisibility(it)
                    it.setSelection(it.text.length)
                }
            }
        }

        // event: Register (validation, error, pass to activity)
        layout.buttonRegister.setOnClickListener {
            layout.tilRegisterEmail.error = null
            layout.tilRegisterPassword.error = null

            layout.tilRegisterEmail.editText?.let { view ->
                val email = view.text.toString()
                if (model.validateEmail(email)) {
                    layout.tilRegisterEmail.error = getString(R.string.fui_invalid_email_address)
                    view.requestFocus()
                    model.email = email
                }
            }

            layout.tilRegisterPassword.editText?.let { view ->
                val password = view.text.toString()
                model.validatePassword(password)
                if (!model.passwordStatus.first) {
                    layout.tilRegisterPassword.error = model.passwordStatus.second
                    view.requestFocus()
                    model.password = password
                }
            }

            if (model.email != null && model.password != null) {
                onPositiveButtonClick()
            }
        }

        // region // clear: ErrorText -> event: user
        layout.tilRegisterPassword.also {
            it.editText?.doOnTextChanged { text, start, before, count ->
                if (it.isErrorEnabled) it.error = null
                Log.d("IGNORE", "Logging params to curb warnings: $text $start $before $count")
            }
        }
        layout.tilRegisterEmail.also {
            it.editText?.doOnTextChanged { text, start, before, count ->
                if (it.isErrorEnabled) it.error = null
                Log.d("IGNORE", "Logging params to curb warnings: $text $start $before $count")
            }
        }
        // endregion

        return layout.root
    }

    // clean up any references to the binding class instance
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}