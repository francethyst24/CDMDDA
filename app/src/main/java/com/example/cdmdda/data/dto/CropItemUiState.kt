package com.example.cdmdda.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CropItemUiState(
    val id: String = String(),
    val name: String = String(),
    val isSupported: Boolean = false,
    val bannerId: Int = 0,
) : CropUiState, Parcelable
