package com.example.cdmdda.model.dto

import android.content.Context
import com.example.cdmdda.view.utils.LocaleUtils

data class Disease (
    val name : String = "",
    val vector : String = "",
    val crops : List<String> = emptyList(),
    val tl_crops : List<String> = emptyList(),
    val cause : String = "",
    val treatment : String = "",
    val symptoms : List<String> = emptyList(),
) {
    fun getCrops(context: Context) : List<String> = when (LocaleUtils.getLanguage(context)) {
        "en" -> crops
        else -> tl_crops
    }
}
