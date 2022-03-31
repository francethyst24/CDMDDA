package com.example.cdmdda.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CropItemUiState(
    override val id: String = String(),
    val name: String = String(),
    val isDiagnosable: Boolean = false,
    val bannerId: Int = 0,
) : CropUiState(id), Parcelable
