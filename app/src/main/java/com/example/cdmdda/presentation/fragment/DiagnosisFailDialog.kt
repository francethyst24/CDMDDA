package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class DiagnosisFailDialog constructor(
    private val hasLeafDetected: Boolean,
    private val onPositiveButtonClick: () -> Unit,
) : AppCompatDialogFragment() {
    companion object {
        const val TAG = "DiagnosisFailDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        val case = getString(if (hasLeafDetected) R.string.ui_text_disease else R.string.ui_text_leaf)
        return requireActivity().run {
            val builder = AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.ui_desc_diagnosis_fail, case))
                setPositiveButton(R.string.ui_text_learn_more) { _, _ -> onPositiveButtonClick() }
                setNegativeButton(android.R.string.ok, null)
            }
            builder.create()
        }
    }


}