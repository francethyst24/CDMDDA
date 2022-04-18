package com.example.cdmdda.data.repository

import android.content.Context
import com.example.cdmdda.common.StringUtils.snakecase
import com.example.cdmdda.data.dto.DiseaseText
import kotlinx.coroutines.withContext

class CropDataRepository(context: Context) : DataRepository(context) {

    suspend fun sciName(cropId: String) = withContext(ioDispatcher) {
        return@withContext getResourceId("$SCI_NAME${cropId.snakecase}", STRING)
    }

    suspend fun description(cropId: String) = withContext(ioDispatcher) {
        return@withContext getResourceId("$DESC${cropId.snakecase}", STRING)
    }

    suspend fun diseasesAcquirable(cropId: String) = withContext(defaultDispatcher) {
        val diseases = mutableListOf<DiseaseText>()
        getStringArrayBy(id = "$DISEASES_ACQUIRABLE${cropId.snakecase}")
            .forEach { diseases.add(DiseaseText(it)) }
        return@withContext diseases.sortedBy { it.displayName(context) }.toList()
    }

    suspend fun isDiagnosable(cropId: String): Boolean = withContext(defaultDispatcher) {
        supportedCrops.contains(cropId)
    }

    suspend fun bannerId(cropId: String): Int = withContext(ioDispatcher) {
        return@withContext getResourceId("$BANNER${cropId.snakecase}", DRAWABLE)
    }

}