package com.example.cdmdda.data.repository

import android.content.Context
import com.example.cdmdda.common.Constants.ARRAY
import com.example.cdmdda.common.Constants.CAUSE
import com.example.cdmdda.common.Constants.CROPS_AFFECTED
import com.example.cdmdda.common.Constants.DISEASE_NAME
import com.example.cdmdda.common.Constants.OFFLINE_IMAGES
import com.example.cdmdda.common.Constants.STRING
import com.example.cdmdda.common.Constants.SUPPORTED_DISEASES
import com.example.cdmdda.common.Constants.SYMPTOMS
import com.example.cdmdda.common.Constants.TREATMENTS
import com.example.cdmdda.common.Constants.VECTOR
import com.example.cdmdda.common.ImageList
import com.example.cdmdda.common.utils.AndroidUtils.getResourceId
import com.example.cdmdda.common.utils.AndroidUtils.getStringArray
import com.example.cdmdda.common.utils.AndroidUtils.getStringArrayBy
import com.example.cdmdda.common.utils.StringUtils.snakecase
import com.example.cdmdda.data.dto.CropText
import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.domain.model.Disease

class DiseaseRepository constructor(
    private val context: Context,
    private val id: String
) {
    fun getItem(): DiseaseItem = DiseaseItem(id, isDetectable)

    fun getDisease(images: ImageList): Disease {
        return Disease(id, vector, cause, symptoms, treatments, cropsAffected, isDetectable, images)
    }

    val name by lazy { context.getResourceId(STRING, "$DISEASE_NAME${id.snakecase}") }

    val vector by lazy { context.getResourceId(STRING, "$VECTOR${id.snakecase}") }

    private val cause by lazy { context.getResourceId(STRING, "$CAUSE${id.snakecase}") }

    private val symptoms by lazy { context.getResourceId(ARRAY, "$SYMPTOMS${id.snakecase}") }

    private val treatments by lazy { context.getResourceId(ARRAY, "$TREATMENTS${id.snakecase}") }

    private val cropsAffected: List<CropText>
        get() {
            val crops = mutableListOf<CropText>()
            context.getStringArrayBy("$CROPS_AFFECTED${id.snakecase}")
                .forEach {
                    val repository = CropRepository(context, it)
                    crops.add(repository.getText())
                }
            return crops
        }

    private val isDetectable by lazy { context.getStringArray(SUPPORTED_DISEASES).contains(id) }

    val imagesArray by lazy { context.getResourceId(ARRAY, "$OFFLINE_IMAGES${id.snakecase}") }

}