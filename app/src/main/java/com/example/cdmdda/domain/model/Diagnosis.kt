package com.example.cdmdda.domain.model

data class Diagnosis(
    val id: String = String(),
    val diagnoseDateString: String = String(),
    val confidenceLvl: Int = Int.MIN_VALUE,
)