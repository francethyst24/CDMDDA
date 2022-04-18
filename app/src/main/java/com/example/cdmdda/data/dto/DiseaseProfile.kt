package com.example.cdmdda.data.dto

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes

data class DiseaseProfile(
    val id: String = String(),
    @StringRes val vector: Int = 0,
    @StringRes val cause: Int = 0,
    @ArrayRes val symptoms: Int = 0,
    @ArrayRes val treatments: Int = 0,
    val cropsAffected: List<CropText> = listOf(),
    val isSupported: Boolean = false,
    val offlineImages: IntArray = intArrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiseaseProfile

        if (id != other.id) return false
        if (vector != other.vector) return false
        if (cause != other.cause) return false
        if (symptoms != other.symptoms) return false
        if (treatments != other.treatments) return false
        if (cropsAffected != other.cropsAffected) return false
        if (isSupported != other.isSupported) return false
        if (!offlineImages.contentEquals(other.offlineImages)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + vector
        result = 31 * result + cause
        result = 31 * result + symptoms
        result = 31 * result + treatments
        result = 31 * result + cropsAffected.hashCode()
        result = 31 * result + isSupported.hashCode()
        result = 31 * result + offlineImages.contentHashCode()
        return result
    }
}
