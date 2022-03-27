package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class DiagnosisFailDialog(private val onPositiveButtonClick: () -> Unit) : AppCompatDialogFragment() {
    companion object {
        const val TAG = "DiagnosisFailDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setTitle(R.string.ui_warn_diagnosis_fail)
                setMessage(R.string.ui_desc_diagnosis_fail)
                setPositiveButton(getString(R.string.ui_text_learn_more)) { dialog, which ->
                    Log.d("IGNORE", "Logging param to curb warnings: $dialog $which")
                    onPositiveButtonClick()
                }
                setNegativeButton(android.R.string.ok, null)
            }
            builder.create()
        }
    }


}