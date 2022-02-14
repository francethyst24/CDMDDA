package com.example.cdmdda.model

import android.content.Context
import com.example.cdmdda.view.utils.ResourceUtils
import com.example.cdmdda.view.utils.StringUtils

class TextRepository(private val context: Context, private val DATASET: String) {
    companion object {
        private const val resType = "string"
    }

    fun fetchCropDescription(cropId: String): String {
        val uri = "$resType/crop_description_${StringUtils.toResourceId(cropId)}"
        val id = context.resources.getIdentifier(uri, resType, context.packageName)
        return context.resources.getString(id)
    }

    fun fetchCropName(cropId: String): String {
        val textCropId = "@string/crop_name_${cropId}"
        return ResourceUtils.getStringById(context, textCropId)
    }

}