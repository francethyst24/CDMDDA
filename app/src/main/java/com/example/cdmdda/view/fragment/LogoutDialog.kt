package com.example.cdmdda.view.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class LogoutDialog : AppCompatDialogFragment() {

    // region // interact: parent
    private lateinit var logoutDialogListener : LogoutDialogListener
    interface LogoutDialogListener {
        fun onLogoutClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logoutDialogListener = context as LogoutDialogListener
    }
    // endregion

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setMessage(R.string.ui_desc_logout)
                setTitle(R.string.ui_text_logout)
                setPositiveButton(R.string.ui_text_logout) { dialog, which ->
                    logoutDialogListener.onLogoutClick()
                    Log.d("IGNORE", "Logging param to curb warnings: $dialog $which")
                }
                setNegativeButton(R.string.ui_text_cancel, null)
            }
            // Create the AlertDialog object and return it
            builder.create()
        }
    }

}