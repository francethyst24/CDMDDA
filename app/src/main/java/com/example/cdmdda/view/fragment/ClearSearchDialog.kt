package com.example.cdmdda.view.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
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
                setMessage(R.string.desc_clear_search)
                setTitle(R.string.title_clear_search)
                setPositiveButton(android.R.string.ok) { dialog, which ->
                    clearSearchDialogListener.onClearSearchClick()
                    Toast.makeText(
                        activity,
                        getString(R.string.text_success_clear_search),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setNegativeButton(R.string.button_cancel, null)
            }
            // Create the AlertDialog object and return it
            builder.create()
        }
    }


}