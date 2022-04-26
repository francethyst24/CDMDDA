package com.example.cdmdda.data.dto

import android.content.Context
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.cdmdda.common.utils.AndroidUtils.getStringCompat
import kotlinx.parcelize.Parcelize

sealed class CropUiState constructor(
    open val id: String,
    @StringRes open val name: Int,
)

@Parcelize
data class CropItem constructor(
    override val id: String = String(),
    @StringRes override val name: Int = 0,
    val isDiagnosable: Boolean = false,
    @DrawableRes val bannerId: Int = 0,
) : CropUiState(id, name), Parcelable

@Parcelize
data class CropText constructor(
    override val id: String = String(),
    @StringRes override val name: Int = 0,
) : CropUiState(id, name), UiState, Parcelable {
    override fun displayName(context: Context) = context.getStringCompat(name)
}