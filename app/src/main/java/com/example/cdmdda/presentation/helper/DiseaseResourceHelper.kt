package com.example.cdmdda.presentation.helper

import android.content.Context
import com.example.cdmdda.common.StringList
import com.example.cdmdda.common.StringUtils.snakecase
import com.example.cdmdda.data.dto.CropText
import kotlinx.coroutines.withContext

class DiseaseResourceHelper(context: Context) : ResourceHelper(context) {

    suspend fun vector(id: String): String = withContext(ioDispatcher) {
        return@withContext getStringBy(id = "$VECTOR${id.snakecase}")
    }

    suspend fun cause(id: String): String = withContext(ioDispatcher) {
        return@withContext getStringBy(id = "$CAUSE${id.snakecase}")
    }

    suspend fun treatments(id: String): StringList = withContext(ioDispatcher) {
        return@withContext getStringArrayBy(id = "$TREATMENTS${id.snakecase}").toList()
    }

    suspend fun symptoms(id: String): StringList = withContext(ioDispatcher) {
        return@withContext getStringArrayBy(id = "$SYMPTOMS${id.snakecase}").toList()
    }

    suspend fun cropsAffected(id: String) = withContext(defaultDispatcher) {
        val crops = mutableListOf<CropText>()
        getStringArrayBy(id = "$CROPS_AFFECTED${id.snakecase}")
            .forEach { crops.add(CropText(it, name(it))) }
        return@withContext crops.toList()
    }

    suspend fun isDetectable(id: String): Boolean = withContext(defaultDispatcher) {
        supportedDiseases.contains(id)
        // context.resources.getStringArray(R.array.string_diseases).contains(id)
    }

    fun offlineImages(id: String): Int {
        return getDrawableArrayIdBy(name = "$OFFLINE_IMAGES${id.snakecase}")
    }

}