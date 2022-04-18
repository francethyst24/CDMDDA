package com.example.cdmdda.presentation.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R
import com.google.firebase.auth.FirebaseUser

class EmailVerificationDialog constructor(
    private val user: FirebaseUser,
    private val onPositiveButtonClick: () -> Unit,
) : AppCompatDialogFragment() {
    companion object { const val TAG = "EmailVerificationDialog" }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        user.reload().addOnCompleteListener { if (it.isSuccessful && user.isEmailVerified) dismiss() }
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setMessage(getString(R.string.ui_desc_verify_email).plus(" ${user.email}"))
                setTitle(R.string.ui_head_verify_email)
                setPositiveButton(R.string.ui_text_logout_instead) { _, _ -> onPositiveButtonClick() }
            }
            builder.create()
        }
    }
}