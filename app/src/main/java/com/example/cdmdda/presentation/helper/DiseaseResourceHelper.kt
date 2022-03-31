package com.example.cdmdda.presentation.helper

import android.content.Context
import com.example.cdmdda.common.StringFormat.toResourceId
import com.example.cdmdda.common.StringList
import com.example.cdmdda.data.dto.CropTextUiState
import kotlinx.coroutines.withContext

class DiseaseResourceHelper(context: Context) : ResourceHelper(context) {

    suspend fun vector(id: String): String = withContext(ioDispatcher) {
        return@withContext getStringBy(id = "$VECTOR${id.toResourceId()}")
    }

    suspend fun cause(id: String): String = withContext(ioDispatcher) {
        return@withContext getStringBy(id = "$CAUSE${id.toResourceId()}")
    }

    suspend fun treatments(id: String): StringList = withContext(ioDispatcher) {
        return@withContext getStringArrayBy(id = "$TREATMENTS${id.toResourceId()}").toList()
    }

    suspend fun symptoms(id: String): StringList = withContext(ioDispatcher) {
        return@withContext getStringArrayBy(id = "$SYMPTOMS${id.toResourceId()}").toList()
    }

    suspend fun cropsAffected(id: String) = withContext(defaultDispatcher) {
        val crops = mutableListOf<CropTextUiState>()
        getStringArrayBy(id = "$CROPS_AFFECTED${id.toResourceId()}")
            .forEach { crops.add(CropTextUiState(it, name(it))) }
        return@withContext crops.toList()
    }

    suspend fun isDetectable(id: String): Boolean = withContext(defaultDispatcher) {
        supportedDiseases.contains(id)
        // context.resources.getStringArray(R.array.string_diseases).contains(id)
    }

    fun offlineImages(id: String): Int {
        return getDrawableArrayIdBy(name = "$OFFLINE_IMAGES${id.toResourceId()}")
    }

}