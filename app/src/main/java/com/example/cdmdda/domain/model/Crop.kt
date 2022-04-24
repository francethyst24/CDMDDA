package com.example.cdmdda.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.cdmdda.data.dto.DiseaseText

data class Crop constructor(
    @StringRes val name: Int = 0,
    @StringRes val sciName: Int = 0,
    @StringRes val desc: Int = 0,
    val diseases: List<DiseaseText> = listOf(),
    val isSupported: Boolean = false,
    @DrawableRes val bannerId: Int = 0,
)
