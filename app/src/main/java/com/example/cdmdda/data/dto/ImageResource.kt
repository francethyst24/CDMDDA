package com.example.cdmdda.data.dto

sealed interface ImageResource {
    data class Integer(val value: Int) : ImageResource
    data class Uri(val value: android.net.Uri) : ImageResource
}
