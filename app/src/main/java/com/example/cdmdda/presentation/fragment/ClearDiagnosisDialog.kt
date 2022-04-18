package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class ClearDiagnosisDialog constructor(
    private val onPositiveButtonClick: () -> Unit,
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().run {
            val builder = AlertDialog.Builder(this).apply {
                setMessage(R.string.ui_warn_clear_diagnosis)
                setTitle(R.string.ui_text_clear_diagnosis)
                setPositiveButton(android.R.string.ok) { _, _ -> onPositiveButtonClick() }
                setNegativeButton(R.string.ui_text_cancel, null)
            }
            builder.create()
        }
    }
}