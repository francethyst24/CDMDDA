package com.example.cdmdda.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiseaseItemUiState(
    override val id: String = String(),
    val isDetectable: Boolean = false,
) : DiseaseUiState(id), Parcelable
