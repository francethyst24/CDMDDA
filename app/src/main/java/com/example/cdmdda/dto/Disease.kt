package com.example.cdmdda.dto

data class Disease (
    var name: String? = null,
    var vector: String? = null,
    var crops: List<String> = ArrayList(),
)
