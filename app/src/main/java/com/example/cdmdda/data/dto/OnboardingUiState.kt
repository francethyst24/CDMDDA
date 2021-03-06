package com.example.cdmdda.data.dto

import androidx.annotation.DrawableRes

data class OnboardingUiState(
    @DrawableRes val imageRes: Int,
    val title: String,
    val descEn: String,
    val descTl: String,
)
