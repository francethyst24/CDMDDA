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
import com.example.cdmdda.databinding.FragmentLoginBinding
import com.example.cdmdda.presentation.utils.toggleTextVisibility
import com.example.cdmdda.presentation.viewmodel.AccountViewModel

class LoginFragment : Fragment() {

    // region // declare: ViewBinding, ViewModel
    private var binding: FragmentLoginBinding? = null
    private val layout get() = binding!!
    private val model: AccountViewModel by activityViewModels()
    // endregion

    // region // interface: FragmentListener
    private lateinit var loginFragmentListener : LoginFragmentListener

    interface LoginFragmentListener {
        fun onLoginClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginFragmentListener = context as LoginFragmentListener
    }
    // endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // init: ViewBinding
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        // set: PasswordText.visibility
        layout.tilLoginPassword.also { textInput ->
            textInput.setEndIconOnClickListener {
                textInput.editText?.let {
                    textInput.toggleTextVisibility(it)
                    it.setSelection(it.text.length)
                }
            }
        }

        // event : Login (validation, error, pass to activity)
        layout.buttonLogin.setOnClickListener {
            layout.tilLoginEmail.error = null
            layout.tilLoginPassword.error = null

            layout.tilLoginEmail.editText?.let { view ->
                val email = view.text.toString()
                if (model.validateEmail(email)) {
                    layout.tilLoginEmail.error = getString(R.string.fui_invalid_email_address)
                    view.requestFocus()
                    model.email = email
                }
            }

            layout.tilLoginPassword.editText?.let { view ->
                val password = view.text.toString()
                model.validatePassword(password)
                if (!model.passwordStatus.first) {
                    layout.tilLoginPassword.error = model.passwordStatus.second
                    view.requestFocus()
                    model.password = password
                }
            }

            if (model.email != null && model.password != null) {
                loginFragmentListener.onLoginClick()
            }
        }

        // region // clear: ErrorText -> event: user
        layout.tilLoginPassword.also {
            it.editText?.doOnTextChanged { text, start, before, count ->
                Log.d("IGNORE", "Logging params to curb warnings: $text $start $before $count")
                if (it.isErrorEnabled) it.error = null
            }
        }

        layout.tilLoginEmail.also {
            it.editText?.doOnTextChanged { text, start, before, count ->
                Log.d("IGNORE", "Logging params to curb warnings: $text $start $before $count")
                if (it.isErrorEnabled) it.error = null
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