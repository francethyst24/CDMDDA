package com.example.cdmdda.data.dto

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CropText(
    override val id: String = String(),
    val name: String = String(),
) : Crop(id), TextUiState, Parcelable {
    @IgnoredOnParcel
    override val displayName: String get() = name
}
