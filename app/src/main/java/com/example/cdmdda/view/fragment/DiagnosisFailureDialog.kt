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
                setTitle(R.string.ui_fail_diagnosis)
                setMessage(R.string.ui_desc_fail_diagnosis)
                setPositiveButton(android.R.string.ok, null)
            }
            builder.create()
        }
    }
}