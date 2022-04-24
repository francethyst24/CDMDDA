package com.example.cdmdda.data.repository

import android.content.Context
import com.example.cdmdda.common.AndroidUtils.getResourceId
import com.example.cdmdda.common.AndroidUtils.getStringArray
import com.example.cdmdda.common.AndroidUtils.getStringArrayBy
import com.example.cdmdda.common.Constants.BANNER
import com.example.cdmdda.common.Constants.CROP_NAME
import com.example.cdmdda.common.Constants.DESC
import com.example.cdmdda.common.Constants.DISEASES_ACQUIRABLE
import com.example.cdmdda.common.Constants.DRAWABLE
import com.example.cdmdda.common.Constants.SCI_NAME
import com.example.cdmdda.common.Constants.STRING
import com.example.cdmdda.common.Constants.SUPPORTED_CROPS
import com.example.cdmdda.common.StringUtils.snakecase
import com.example.cdmdda.data.dto.CropItem
import com.example.cdmdda.data.dto.CropText
import com.example.cdmdda.data.dto.CropUiState
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.domain.model.Crop

class CropRepository constructor(
    private val context: Context,
    private val id: String,
) {
    fun getText(): CropText = CropText(id, name)

    fun getItem(): CropItem = CropItem(id, name, isDiagnosable, bannerId)

    fun getCrop(dto: CropUiState? = null): Crop {
        val name = dto?.name ?: name
        val isDiagnosable = if (dto is CropItem) dto.isDiagnosable else isDiagnosable
        val bannerId      = if (dto is CropItem) dto.bannerId      else bannerId
        return Crop(name, sciName, description, diseasesAcquirable, isDiagnosable, bannerId)
    }

    val name by lazy {
        context.getResourceId(STRING, "$CROP_NAME${id.snakecase}")
    }

    private val sciName by lazy {
        context.getResourceId(STRING, "$SCI_NAME${id.snakecase}")
    }

    private val description by lazy {
        context.getResourceId(STRING, "$DESC${id.snakecase}")
    }

    private val diseasesAcquirable: List<DiseaseText>
        get() {
            val diseases = mutableListOf<DiseaseText>()
            context.getStringArrayBy("$DISEASES_ACQUIRABLE${id.snakecase}")
                .forEach {
                    val diseaseText = DiseaseText(id = it)
                    diseases.add(diseaseText)
                }
            return diseases.sortedBy { it.displayName(context) }
        }

    private val isDiagnosable by lazy {
        context.getStringArray(SUPPORTED_CROPS).contains(id)
    }

    private val bannerId by lazy {
        context.getResourceId(DRAWABLE, "$BANNER${id.snakecase}")
    }

}