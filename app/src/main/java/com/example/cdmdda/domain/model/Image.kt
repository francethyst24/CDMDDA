package com.example.cdmdda.domain.model

sealed interface Image {
    data class Res(val resId: Int) : Image
    data class Uri(val value: android.net.Uri) : Image
}
