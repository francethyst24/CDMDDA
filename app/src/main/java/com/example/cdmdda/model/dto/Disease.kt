package com.example.cdmdda.model.dto

import android.content.Context
import com.example.cdmdda.view.LocaleHelper

data class Disease (
    val name : String = "",
    val vector : String = "",
    val crops : List<String> = emptyList(),
    val tl_crops : List<String> = emptyList(),
) {
    fun getCrops(context: Context) : List<String> = when (LocaleHelper.getLanguage(context)) {
        "en" -> crops
        else -> tl_crops
    }
}
