package com.example.cdmdda.fragments

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.cdmdda.R
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val parentView : View? = inflater.inflate(
            R.layout.fragment_login, container, false)
        val editPass : EditText = parentView!!.findViewById(R.id.edit_login_pass)
        val inputLayout : TextInputLayout = parentView!!.findViewById(R.id.til_login_pass)

        //      toggleable password visibility
        inputLayout.setEndIconOnClickListener {
            if (editPass.transformationMethod != null) {
                inputLayout.setEndIconDrawable(R.drawable.ic_baseline_visibility_24)
                editPass.transformationMethod = null
            } else {
                inputLayout.setEndIconDrawable(R.drawable.ic_baseline_visibility_off_24)
                editPass.transformationMethod = PasswordTransformationMethod()
            }
            editPass.setSelection(editPass.text.length)
        }



        return parentView
    }

}