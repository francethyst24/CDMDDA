package com.example.cdmdda.presentation.helper

import android.content.Context
import com.example.cdmdda.R
import com.example.cdmdda.data.dto.CropTextUiState
import com.example.cdmdda.data.dto.DiseaseTextUiState
import com.example.cdmdda.presentation.utils.ResourceUtils
import com.example.cdmdda.presentation.utils.toResourceId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResourceHelper(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun fetchCropName(cropId: String): String = withContext(ioDispatcher) {
        val uri = "crop_name_${cropId.toResourceId()}"
        return@withContext ResourceUtils.getStringById(context, uri)
    }

    suspend fun fetchCropDescription(cropId: String): String = withContext(ioDispatcher) {
        val uri = "crop_desc_${cropId.toResourceId()}"
        return@withContext ResourceUtils.getStringById(context, uri)
    }

    suspend fun fetchCropDiseasesAcquirable(cropId: String): List<DiseaseTextUiState> = withContext(ioDispatcher) {
        val uri = "string_diseases_${cropId.toResourceId()}"
        val diseasesAcquirable = mutableListOf<DiseaseTextUiState>()
        ResourceUtils.getStringArrayById(context, uri).forEach {
            diseasesAcquirable.add(DiseaseTextUiState(it))
        }
        return@withContext diseasesAcquirable
    }

    suspend fun fetchCropSciName(cropId: String): String = withContext(ioDispatcher) {
        val uri = "crop_sci_name_${cropId.toResourceId()}"
        return@withContext ResourceUtils.getStringById(context, uri)
    }

    suspend fun fetchDiseaseVector(diseaseId: String): String = withContext(ioDispatcher) {
        val uri = "disease_vector_${diseaseId.toResourceId()}"
        return@withContext ResourceUtils.getStringById(context, uri)
    }

    suspend fun fetchDiseaseCause(diseaseId: String): String = withContext(ioDispatcher) {
        val uri = "disease_cause_${diseaseId.toResourceId()}"
        return@withContext ResourceUtils.getStringById(context, uri)
    }

    suspend fun fetchDiseaseTreatment(diseaseId: String): List<String> = withContext(ioDispatcher) {
        val uri = "string_treatments_${diseaseId.toResourceId()}"
        return@withContext ResourceUtils.getStringArrayById(context, uri).toList()
    }

    suspend fun fetchDiseaseSymptoms(diseaseId: String): List<String> = withContext(ioDispatcher) {
        val uri = "string_symptoms_${diseaseId.toResourceId()}"
        return@withContext ResourceUtils.getStringArrayById(context, uri).toList()
    }

    suspend fun fetchDiseaseCropsAffected(diseaseId: String): List<CropTextUiState> = withContext(ioDispatcher) {
        val uri = "string_crops_${diseaseId.toResourceId()}"
        val cropsAffected = mutableListOf<CropTextUiState>()
        ResourceUtils.getStringArrayById(context, uri).forEach {
            cropsAffected.add(CropTextUiState(it, fetchCropName(it)))
        }
        return@withContext cropsAffected
    }

    suspend fun fetchCropsSupported(): Array<String> = withContext(ioDispatcher) {
        return@withContext context.resources.getStringArray(R.array.string_crops)
    }

    suspend fun fetchDiseasesSupported(): Array<String> = withContext(ioDispatcher) {
        return@withContext context.resources.getStringArray(R.array.string_diseases)
    }

    suspend fun fetchCropIsSupported(cropId: String): Boolean = withContext(ioDispatcher) {
        context.resources.getStringArray(R.array.string_crops).contains(cropId)
    }

    suspend fun fetchDiseaseIsSupported(diseaseId: String): Boolean = withContext(ioDispatcher) {
        context.resources.getStringArray(R.array.string_diseases).contains(diseaseId)
    }

    suspend fun fetchCropBannerId(cropId: String): Int = withContext(ioDispatcher) {
        val uri = "banner_${cropId.toResourceId()}"
        return@withContext ResourceUtils.getDrawableId(context, uri)
    }

    fun fetchDiseaseImages(diseaseId: String): Int {
        val uri = "drawables_${diseaseId.toResourceId()}"
        return ResourceUtils.getDrawableArrayId(context, uri)
    }

}