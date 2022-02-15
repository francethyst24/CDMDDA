package com.example.cdmdda.model.dto

data class CropItem(
    val name: String,
    val sciName: String,
    val desc: String,
    val diseases: List<String>,
)
