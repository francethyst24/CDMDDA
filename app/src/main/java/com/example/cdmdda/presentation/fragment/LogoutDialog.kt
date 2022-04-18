package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class LogoutDialog constructor(
    private val onPositiveButtonClick: () -> Unit,
) : AppCompatDialogFragment() {
    companion object { const val TAG = "LogoutDialog" }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return requireActivity().run {
            val builder = AlertDialog.Builder(this).apply {
                setMessage(R.string.ui_desc_logout)
                setTitle(R.string.ui_text_logout)
                setPositiveButton(R.string.ui_text_logout) { _, _ -> onPositiveButtonClick() }
                setNegativeButton(R.string.ui_text_cancel, null)
            }
            builder.create()
        }
    }

}