package com.example.cdmdda.view.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class ClearSearchDialog : AppCompatDialogFragment() {

    // region // interact: parent
    private lateinit var clearSearchDialogListener : ClearSearchDialogListener
    interface ClearSearchDialogListener {
        fun onClearSearchClick()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        clearSearchDialogListener = context as ClearSearchDialogListener
    }
    // endregion

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setMessage(R.string.ui_warn_clear_search)
                setTitle(R.string.ui_text_clear_search)
                setPositiveButton(android.R.string.ok) { dialog, which ->
                    clearSearchDialogListener.onClearSearchClick()
                    Toast.makeText(
                        activity,
                        getString(R.string.ui_text_clear_search_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("IGNORE", "Logging param to curb warnings: $dialog $which")
                }
                setNegativeButton(R.string.ui_text_cancel, null)
            }
            // Create the AlertDialog object and return it
            builder.create()
        }
    }


}