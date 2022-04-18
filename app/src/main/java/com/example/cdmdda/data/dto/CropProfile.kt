package com.example.cdmdda.data.dto

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CropProfile(
    @StringRes val name: Int = 0,
    @StringRes val sciName: Int = 0,
    @StringRes val desc: Int = 0,
    val diseases: List<DiseaseText> = listOf(),
    val isSupported: Boolean = false,
    @DrawableRes val bannerId: Int = 0,
)
