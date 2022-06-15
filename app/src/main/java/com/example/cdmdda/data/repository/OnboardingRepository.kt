package com.example.cdmdda.data.repository

import android.content.Context
import androidx.annotation.ArrayRes
import com.example.cdmdda.common.utils.AndroidUtils.getStringArray
import com.example.cdmdda.data.dto.OnboardingUiState

class OnboardingRepository constructor(
    private val context: Context,
) {
    fun getAllOnboarding(
        @ArrayRes imagesId: Int,
        @ArrayRes titlesId: Int,
        @ArrayRes descEnId: Int,
        @ArrayRes descTlId: Int,
    ): List<OnboardingUiState> {
        val images = context.resources.obtainTypedArray(imagesId)
        val titles = context.getStringArray(titlesId)
        val descEn = context.getStringArray(descEnId)
        val descTl = context.getStringArray(descTlId)
        val emitList = mutableListOf<OnboardingUiState>()
        (0 until 3).forEach {
            val tba = OnboardingUiState(
                images.getResourceId(it, 0),
                titles[it],
                descEn[it],
                descTl[it],
            )
            emitList.add(tba)
        }
        images.recycle()
        return emitList
    }
}