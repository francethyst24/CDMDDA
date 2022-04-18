package com.example.cdmdda.data.repository

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import com.example.cdmdda.common.StringUtils.snakecase
import com.example.cdmdda.data.dto.CropText
import kotlinx.coroutines.withContext

class DiseaseDataRepository(context: Context) : DataRepository(context) {

    suspend fun vector(id: String) = withContext(ioDispatcher) {
        return@withContext getResourceId("$VECTOR${id.snakecase}", STRING)
    }

    suspend fun cause(id: String) = withContext(ioDispatcher) {
        return@withContext getResourceId("$CAUSE${id.snakecase}", STRING)
    }

    suspend fun symptoms(id: String) = withContext(ioDispatcher) {
        return@withContext getResourceId("$SYMPTOMS${id.snakecase}", ARRAY)
    }

    suspend fun treatments(id: String) = withContext(ioDispatcher) {
        return@withContext getResourceId("$TREATMENTS${id.snakecase}", ARRAY)
    }

    suspend fun cropsAffected(id: String) = withContext(defaultDispatcher) {
        val crops = mutableListOf<CropText>()
        getStringArrayBy(id = "$CROPS_AFFECTED${id.snakecase}")
            .forEach { crops.add(CropText(it, name(it))) }
        return@withContext crops.toList()
    }

    suspend fun isDetectable(id: String): Boolean = withContext(defaultDispatcher) {
        supportedDiseases.contains(id)
    }

    suspend fun offlineImages(id: String) = withContext(ioDispatcher) {
        val resId = getResourceId("$OFFLINE_IMAGES${id.snakecase}", ARRAY)
        val typedArray: TypedArray
        try {
            typedArray = context.resources.obtainTypedArray(resId)
        } catch (e: Resources.NotFoundException) { return@withContext intArrayOf() }
        val intList = mutableListOf<Int>()
        for (i in 0 until typedArray.length()) {
            intList.add(typedArray.getResourceId(i, 0))
        }
        typedArray.recycle()
        return@withContext intList.toIntArray()
    }

}