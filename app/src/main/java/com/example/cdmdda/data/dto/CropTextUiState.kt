package com.example.cdmdda.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CropTextUiState(
    val id: String = String(),
    val name: String = String(),
) : TextUiState, CropUiState, Parcelable
