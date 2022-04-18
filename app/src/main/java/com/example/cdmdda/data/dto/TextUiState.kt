package com.example.cdmdda.data.dto

import android.content.Context
import androidx.annotation.StringRes

sealed interface TextUiState {
    fun displayName(context: Context): String
}
