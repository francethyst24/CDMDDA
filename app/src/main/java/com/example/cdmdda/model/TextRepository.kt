package com.example.cdmdda.model

import android.content.Context
import com.example.cdmdda.view.utils.ResourceUtils
import com.example.cdmdda.view.utils.StringUtils

class TextRepository(private val context: Context) {

    fun fetchCropName(cropId: String): String {
        val uri = "crop_name_${StringUtils.toResourceId(cropId)}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchCropDescription(cropId: String): String {
        val uri = "crop_desc_${StringUtils.toResourceId(cropId)}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchCropDiseases(cropId: String): List<String> {
        val uri = "string_diseases_${StringUtils.toResourceId(cropId)}"
        return ResourceUtils.getStringArrayById(context, uri).toList()
    }

    fun fetchCropSciName(cropId: String): String {
        val uri = "crop_sci_name_${StringUtils.toResourceId(cropId)}"
        return ResourceUtils.getStringById(context, uri)
    }

}