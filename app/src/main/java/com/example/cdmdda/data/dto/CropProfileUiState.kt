package com.example.cdmdda.data.dto

data class CropProfileUiState(
    val name: String = String(),
    val sciName: String = String(),
    val desc: String = String(),
    val diseases: List<DiseaseTextUiState> = listOf(),
    val isSupported: Boolean = false,
    val bannerId: Int = 0,
)
