package com.example.cdmdda.model.dto

import android.text.SpannableString

data class DiseaseItem(
    val name: String,
    val isSupported: Boolean,
    val highlightedName: SpannableString,
)
