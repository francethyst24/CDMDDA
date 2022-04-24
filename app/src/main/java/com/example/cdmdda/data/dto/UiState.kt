package com.example.cdmdda.data.dto

import android.content.Context

sealed interface UiState {
    fun displayName(context: Context): String
}
