package com.example.cdmdda.model.dto

import android.content.Context
import com.example.cdmdda.view.LocaleHelper

data class Crop (
    val name : String = "",
    val tl_name: String = "",
    val sci_name : String = "",
    val diseases : List<String> = emptyList(),
) {
    fun getName(context: Context) : String = when (LocaleHelper.getLanguage(context)){
        "en" -> name
        else -> tl_name
    }
}