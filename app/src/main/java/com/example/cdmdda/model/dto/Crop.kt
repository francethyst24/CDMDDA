package com.example.cdmdda.model.dto

data class Crop (
    var name: String? = null,
    var sci_name: String? = null,
    var diseases: List<String> = ArrayList(),
)
