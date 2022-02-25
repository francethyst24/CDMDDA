package com.example.cdmdda.model.dto

data class Crop(
    val name: String,
    val sciName: String,
    val desc: String,
    val diseases: List<String>,
)
