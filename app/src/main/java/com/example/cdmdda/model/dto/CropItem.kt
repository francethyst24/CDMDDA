package com.example.cdmdda.model.dto

import android.graphics.drawable.Drawable

data class CropItem(
    val cropId: String,
    val name: String,
    val isSupported: Boolean,
    var banner: Drawable?,
)
