package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class LogoutDialog(private val onPositiveButtonClick: () -> Unit) : AppCompatDialogFragment() {
    companion object {
        const val TAG = "LogoutDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setMessage(R.string.ui_desc_logout)
                setTitle(R.string.ui_text_logout)
                setPositiveButton(R.string.ui_text_logout) { dialog, which ->
                    Log.d("IGNORE", "Logging param to curb warnings: $dialog $which")
                    onPositiveButtonClick()
                }
                setNegativeButton(R.string.ui_text_cancel, null)
            }
            // Create the AlertDialog object and return it
            builder.create()
        }
    }

}