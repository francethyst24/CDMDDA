package com.example.cdmdda.data.dto

data class CropProfile(
    val name: String = String(),
    val sciName: String = String(),
    val desc: String = String(),
    val diseases: List<DiseaseText> = listOf(),
    val isSupported: Boolean = false,
    val bannerId: Int = 0,
)
