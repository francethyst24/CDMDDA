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

    fun fetchDiseaseName(diseaseId: String): String = diseaseId

    fun fetchDiseaseVector(diseaseId: String): String {
        val uri = "disease_vector_${StringUtils.toResourceId(diseaseId)}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchDiseaseCause(diseaseId: String): String {
        val uri = "disease_cause_${StringUtils.toResourceId(diseaseId)}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchDiseaseTreatment(diseaseId: String): String {
        val uri = "disease_treatment_${StringUtils.toResourceId(diseaseId)}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchDiseaseSymptoms(diseaseId: String): List<String> {
        val uri = "string_symptoms_${StringUtils.toResourceId(diseaseId)}"
        return ResourceUtils.getStringArrayById(context, uri).toList()
    }

    fun fetchDiseaseCropIds(diseaseId: String): List<String> {
        val uri = "string_crops_${StringUtils.toResourceId(diseaseId)}"
        return ResourceUtils.getStringArrayById(context, uri).toList()
    }

    fun fetchDiseaseCropNames(diseaseId: String): List<String> {
        return mutableListOf<String>().apply {
            fetchDiseaseCropIds(diseaseId).forEach {
                add(fetchCropName(it))
            }
        }
    }

}