package com.example.cdmdda.data.dto

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiseaseText(
    override val id: String = String(),
) : Disease(id), TextUiState, Parcelable {
    @IgnoredOnParcel
    override val displayName: String get() = id
}
