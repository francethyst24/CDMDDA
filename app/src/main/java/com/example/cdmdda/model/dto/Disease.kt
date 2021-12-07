package com.example.cdmdda.model.dto

import android.content.Context
import com.example.cdmdda.view.utils.LocaleUtils

data class Disease (
    val name : String = String(),
    val vector : String = String(),
    val crops : List<String> = emptyList(),
    val tl_crops : List<String> = emptyList(),
    val cause : String = String(),
    val treatment : String = String(),
    val symptoms : List<String> = emptyList(),
) {
    fun getCrops(context: Context) : List<String> = when (LocaleUtils.getLanguage(context)) {
        "en" -> crops
        else -> tl_crops
    }
}
