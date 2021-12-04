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
                setMessage(R.string.desc_clear_diagnosis)
                setTitle(R.string.title_clear_diagnosis)
                setPositiveButton(android.R.string.ok) { dialog, which ->
                    clearDiagnosisDialogListener.onClearDiagnosisClick()
                    Log.d("IGNORE", "Logging param to curb warnings: $dialog $which")
                }
                setNegativeButton(R.string.button_cancel, null)
            }
            // Create the AlertDialog object and return it
            builder.create()
        }
    }
}