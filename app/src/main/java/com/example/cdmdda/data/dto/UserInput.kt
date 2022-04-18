package com.example.cdmdda.data.dto

sealed interface UserInput {
    data class Bmp(val value: android.graphics.Bitmap) : UserInput
    data class Uri(val value: android.net.Uri) : UserInput
}