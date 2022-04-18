package com.example.cdmdda.presentation.helper

import android.content.Context
import com.example.cdmdda.common.StringUtils.snakecase
import com.example.cdmdda.data.dto.DiseaseText
import kotlinx.coroutines.withContext

class CropResourceHelper(context: Context) : ResourceHelper(context) {

    suspend fun description(cropId: String): String = withContext(ioDispatcher) {
        return@withContext getStringBy(id = "$DESC${cropId.snakecase}")
    }

    suspend fun diseasesAcquirable(cropId: String) = withContext(defaultDispatcher) {
        val diseases = mutableListOf<DiseaseText>()
        getStringArrayBy(id = "$DISEASES_ACQUIRABLE${cropId.snakecase}")
            .forEach { diseases.add(DiseaseText(it)) }
        return@withContext diseases.sortedBy { it.displayName }.toList()
    }

    suspend fun sciName(cropId: String): String = withContext(ioDispatcher) {
        return@withContext getStringBy(id = "$SCI_NAME${cropId.snakecase}")
    }

    suspend fun isDiagnosable(cropId: String): Boolean = withContext(defaultDispatcher) {
        supportedCrops.contains(cropId)
    }

    suspend fun bannerId(cropId: String): Int = withContext(ioDispatcher) {
        return@withContext getDrawableIdBy(name = "$BANNER${cropId.snakecase}")
    }

}