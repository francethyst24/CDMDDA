package com.example.cdmdda.data.dto

data class DiseaseProfileUiState(
    val id: String = String(),
    val vector: String = String(),
    val cause: String = String(),
    val symptoms: List<String> = listOf(),
    val treatments: List<String> = listOf(),
    val cropsAffected: List<CropTextUiState> = listOf(),
    val isSupported: Boolean = false,
)
