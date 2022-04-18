package com.example.cdmdda.data.dto

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class CropItem(
    override val id: String = String(),
    @StringRes val name: Int = 0,
    val isDiagnosable: Boolean = false,
    @DrawableRes val bannerId: Int = 0,
) : Crop(id), Parcelable
