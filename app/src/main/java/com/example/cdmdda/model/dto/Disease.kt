package com.example.cdmdda.model.dto

data class Disease(
    val name: String,
    val vector: String,
    val cause: String,
    val treatment: String,
    val symptoms: List<String>,
    val cropIds: List<String>,
    val cropNames: List<String>,
)
