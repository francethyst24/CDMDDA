package com.example.cdmdda.data.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiseaseItem(
    override val id: String = String(),
    val isDetectable: Boolean = false,
) : Disease(id), Parcelable
