package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.cdmdda.R
import com.example.cdmdda.data.UserApi

class LogoutDialogViewModel constructor(
) : ViewModel() {
    val uiWarnLogout by lazy { R.string.ui_text_warn_logout }
    val uiDescLogout by lazy { R.string.ui_desc_logout }
    val uiTextLogout by lazy { R.string.ui_text_logout }
    val uiTextCancel by lazy { R.string.ui_text_cancel }

    fun signOut(context: Context) = UserApi.signOut(context)
}