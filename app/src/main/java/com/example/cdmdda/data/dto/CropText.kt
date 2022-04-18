package com.example.cdmdda.data.dto

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class CropText(
    override val id: String = String(),
    @StringRes val name: Int = 0,
) : Crop(id), TextUiState, Parcelable {
    override fun displayName(context: Context) = context.getString(name)
}
