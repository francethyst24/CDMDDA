package com.example.cdmdda.view.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.R

class LogoutFragment : AppCompatDialogFragment() {

    // region -- LogoutFragment interface
    private lateinit var logoutFragmentListener : LogoutFragmentListener

    interface LogoutFragmentListener {
        fun onLogoutClick(fragment: AppCompatDialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logoutFragmentListener = context as LogoutFragmentListener
    }
    // endregion

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(R.string.desc_logout)
                setTitle(R.string.text_logout)
                setPositiveButton(R.string.text_logout) { dialog, which ->
                    logoutFragmentListener.onLogoutClick(this@LogoutFragment)
                }
                setNegativeButton(R.string.fui_cancel, null)
            }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}