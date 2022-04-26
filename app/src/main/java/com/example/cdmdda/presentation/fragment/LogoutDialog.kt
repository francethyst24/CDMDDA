package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import com.example.cdmdda.presentation.viewmodel.factory.LogoutViewModel

class LogoutDialog : AppCompatDialogFragment() {
    interface OnLogoutListener {
        fun logout()
    }

    private var listener: OnLogoutListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnLogoutListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        const val TAG = "LogoutDialog"
    }

    private val viewModel: LogoutViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = with(viewModel) {
        isCancelable = false
        val builder = AlertDialog.Builder(requireActivity())
            .setTitle(uiTextLogout)
            .setMessage(uiDescLogout)
            .setNegativeButton(uiTextCancel, null)
            .setPositiveButton(uiTextLogout) { _, _ ->
                listener?.logout()
            }
        return builder.create()
    }

}