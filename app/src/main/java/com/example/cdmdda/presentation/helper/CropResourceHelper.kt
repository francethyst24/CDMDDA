package com.example.cdmdda.presentation.helper

import android.content.Context
import com.example.cdmdda.common.StringFormat.toResourceId
import com.example.cdmdda.data.dto.DiseaseTextUiState
import kotlinx.coroutines.withContext

class CropResourceHelper(context: Context) : ResourceHelper(context) {

    suspend fun description(cropId: String): String = withContext(ioDispatcher) {
        return@withContext getStringBy(id = "$DESC${cropId.toResourceId()}")
    }

    suspend fun diseasesAcquirable(cropId: String) = withContext(defaultDispatcher) {
        val diseases = mutableListOf<DiseaseTextUiState>()
        getStringArrayBy(id = "$DISEASES_ACQUIRABLE${cropId.toResourceId()}")
            .forEach { diseases.add(DiseaseTextUiState(it)) }
        return@withContext diseases.toList()
    }

    suspend fun sciName(cropId: String): String = withContext(ioDispatcher) {
        return@withContext getStringBy(id = "$SCI_NAME${cropId.toResourceId()}")
    }

    suspend fun isDiagnosable(cropId: String): Boolean = withContext(defaultDispatcher) {
        supportedCrops.contains(cropId)
    }

    suspend fun bannerId(cropId: String): Int = withContext(ioDispatcher) {
        return@withContext getDrawableIdBy(name = "$BANNER${cropId.toResourceId()}")
    }

}