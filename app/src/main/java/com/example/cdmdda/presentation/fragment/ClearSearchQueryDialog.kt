package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.helper.ThemeHelper
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel
import com.example.cdmdda.presentation.viewmodel.factory.activityViewModelBuilder

class ClearSearchQueryDialog : AppCompatDialogFragment() {
    companion object {
        const val TAG = "ClearSearchQueryDialog"
    }
    private val viewModel by activityViewModelBuilder {
        SettingsViewModel(
            ThemeHelper.getTheme(this),
            LocaleHelper.getLocale(this).toString(),
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = with(viewModel) {
        val builder = AlertDialog.Builder(requireActivity())
            .setTitle(uiTextClearSearch)
            .setMessage(uiWarnClearSearch)
            .setNegativeButton(uiTextCancel, null)
            .setPositiveButton(uiTextOk) { _, _ ->
                user?.let { confirmClearSearch(it.uid) }
            }
        return builder.create()
    }


}