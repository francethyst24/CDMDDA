package com.example.cdmdda.model.dto

import android.content.Context
import com.example.cdmdda.view.utils.LocaleUtils

data class Crop (
    val name : String = String(),
    val tl_name: String = String(),
    val sci_name : String = String(),
    val desc : String = String(),
    val diseases : List<String> = emptyList(),
) {
    fun getName(context: Context) : String = when (LocaleUtils.getLanguage(context)){
        "en" -> name
        else -> tl_name
    }
}