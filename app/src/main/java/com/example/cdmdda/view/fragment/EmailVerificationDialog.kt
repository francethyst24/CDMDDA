package com.example.cdmdda.view.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.firebase.auth.FirebaseUser

class EmailVerificationDialog(private val user: FirebaseUser) : AppCompatDialogFragment() {

    private lateinit var emailVerificationDialogListener: EmailVerificationDialogListener
    interface EmailVerificationDialogListener {
        fun onLogoutInsteadClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        emailVerificationDialogListener = context as EmailVerificationDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        user.reload().addOnCompleteListener { task ->
            if (task.isSuccessful && user.isEmailVerified) this@EmailVerificationDialog.dismiss()
        }
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setMessage("Verify account to proceed. Email sent to".plus(" ${user.email}"))
                setTitle("Email Verification")
                setPositiveButton("Logout instead") { dialog, which ->
                    Log.d("IGNORE", "Logging param to curb warnings: $dialog $which")
                    emailVerificationDialogListener.onLogoutInsteadClick()
                }
            }
            builder.create()
        }
    }
}