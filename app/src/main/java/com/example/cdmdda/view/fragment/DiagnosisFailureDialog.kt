package com.example.cdmdda.view.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class DiagnosisFailureDialog : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it).apply {
                setTitle(R.string.dialog_title_undiagnosed)
                setMessage(R.string.dialog_message_undiagnosed)
                setPositiveButton(R.string.dialog_button_ok, null)
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}