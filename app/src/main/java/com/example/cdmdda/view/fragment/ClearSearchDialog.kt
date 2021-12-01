package com.example.cdmdda.view.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class ClearSearchDialog : AppCompatDialogFragment() {

    // region // interact: parent
    private lateinit var clearSearchDialogListener : ClearSearchDialogListener
    interface ClearSearchDialogListener {
        fun onClearSearchClick(fragment: AppCompatDialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    // endregion

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return requireActivity().let {
            val builder = AlertDialog.Builder(it).apply {
                setMessage(R.string.desc_clear_search)
                setTitle(R.string.title_clear_search)
                setPositiveButton(R.string.dialog_button_ok) { dialog, which ->
                    clearSearchDialogListener.onClearSearchClick(this@ClearSearchDialog)
                }
                setNegativeButton(R.string.fui_cancel, null)
            }
            // Create the AlertDialog object and return it
            builder.create()
        }
    }
}