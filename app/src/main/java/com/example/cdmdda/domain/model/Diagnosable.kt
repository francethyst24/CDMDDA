package com.example.cdmdda.domain.model

sealed interface Diagnosable {
    data class Bmp(val value: android.graphics.Bitmap) : Diagnosable
    data class Uri(val value: android.net.Uri) : Diagnosable
}