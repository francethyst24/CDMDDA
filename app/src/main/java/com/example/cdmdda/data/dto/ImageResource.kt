package com.example.cdmdda.data.dto

sealed interface ImageResource {
    data class Res(val resId: Int) : ImageResource
    data class Uri(val value: android.net.Uri) : ImageResource
}
