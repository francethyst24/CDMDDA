package com.example.cdmdda.domain.model

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import com.example.cdmdda.common.ImageList
import com.example.cdmdda.data.dto.CropText

data class Disease constructor(
    val id: String = String(),
    @StringRes val vector: Int = 0,
    @StringRes val cause: Int = 0,
    @ArrayRes val symptoms: Int = 0,
    @ArrayRes val treatments: Int = 0,
    val cropsAffected: List<CropText> = listOf(),
    val isSupported: Boolean = false,
    val offlineImages: ImageList = listOf(),
)
