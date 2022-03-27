package com.example.cdmdda.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiseaseTextUiState(
    override val id: String = String(),
) : DiseaseUiState(id), TextUiState, Parcelable
