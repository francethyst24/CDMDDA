package com.example.cdmdda.view.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class LogoutDialog : AppCompatDialogFragment() {

    // region // interact: parent
    private lateinit var logoutDialogListener : LogoutDialogListener
    interface LogoutDialogListener {
        fun onLogoutClick(fragment: AppCompatDialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logoutDialogListener = context as LogoutDialogListener
    }
    // endregion

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it).apply {
                setMessage(R.string.desc_logout)
                setTitle(R.string.text_logout)
                setPositiveButton(R.string.text_logout) { dialog, which ->
                    logoutDialogListener.onLogoutClick(this@LogoutDialog)
                }
                setNegativeButton(R.string.fui_cancel, null)
            }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}