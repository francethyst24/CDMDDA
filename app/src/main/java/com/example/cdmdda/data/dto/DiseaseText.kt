package com.example.cdmdda.data.dto

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiseaseText(
    override val id: String = String(),
) : Disease(id), TextUiState, Parcelable {
    override fun displayName(context: Context) = id
}
