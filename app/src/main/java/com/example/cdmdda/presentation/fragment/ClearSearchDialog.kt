package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class ClearSearchDialog(
    private val onPositiveButtonClick: () -> Unit,
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setMessage(R.string.ui_warn_clear_search)
                setTitle(R.string.ui_text_clear_search)
                setPositiveButton(android.R.string.ok) { dialog, which ->
                    Log.d("IGNORE", "Logging param to curb warnings: $dialog $which")
                    onPositiveButtonClick()
                }
                setNegativeButton(R.string.ui_text_cancel, null)
            }
            builder.create()
        }
    }


}