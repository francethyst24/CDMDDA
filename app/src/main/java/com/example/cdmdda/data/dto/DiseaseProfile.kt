package com.example.cdmdda.data.dto

data class DiseaseProfile(
    val id: String = String(),
    val vector: String = String(),
    val cause: String = String(),
    val symptoms: List<String> = listOf(),
    val treatments: List<String> = listOf(),
    val cropsAffected: List<CropText> = listOf(),
    val isSupported: Boolean = false,
    val offlineImages: Int = 0,
)
