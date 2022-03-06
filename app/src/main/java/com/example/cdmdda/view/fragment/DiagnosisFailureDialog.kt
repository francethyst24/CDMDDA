package com.example.cdmdda.view.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class DiagnosisFailureDialog : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setTitle(R.string.ui_warn_diagnosis_fail)
                setMessage(R.string.ui_desc_diagnosis_fail)
                setPositiveButton(android.R.string.ok, null)
            }
            builder.create()
        }
    }
}