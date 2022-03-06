package com.example.cdmdda.view.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class ClearDiagnosisDialog : AppCompatDialogFragment() {
    // region // interact: parent
    private lateinit var clearDiagnosisDialogListener : ClearDiagnosisDialogListener
    interface ClearDiagnosisDialogListener {
        fun onClearDiagnosisClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        clearDiagnosisDialogListener = context as ClearDiagnosisDialogListener
    }
    // endregion

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setMessage(R.string.ui_warn_clear_diagnosis)
                setTitle(R.string.ui_text_clear_diagnosis)
                setPositiveButton(android.R.string.ok) { dialog, which ->
                    Log.d("IGNORE", "Logging param to curb warnings: $dialog $which")
                    clearDiagnosisDialogListener.onClearDiagnosisClick()
                }
                setNegativeButton(R.string.ui_text_cancel, null)
            }
            // Create the AlertDialog object and return it
            builder.create()
        }
    }
}