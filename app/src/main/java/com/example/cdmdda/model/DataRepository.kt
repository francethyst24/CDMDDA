package com.example.cdmdda.model

import android.content.Context
import com.example.cdmdda.R
import com.example.cdmdda.view.utils.ResourceUtils
import com.example.cdmdda.view.utils.toResourceId

class DataRepository(private val context: Context) {

    fun getString(id: Int): String {
        return context.resources.getString(id)
    }

    fun fetchCropName(cropId: String): String {
        val uri = "crop_name_${cropId.toResourceId()}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchCropDescription(cropId: String): String {
        val uri = "crop_desc_${cropId.toResourceId()}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchCropDiseases(cropId: String): List<String> {
        val uri = "string_diseases_${cropId.toResourceId()}"
        return ResourceUtils.getStringArrayById(context, uri).toList()
    }

    fun fetchCropSciName(cropId: String): String {
        val uri = "crop_sci_name_${cropId.toResourceId()}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchDiseaseName(diseaseId: String): String {
        val uri = "disease_${diseaseId.toResourceId()}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchDiseaseVector(diseaseId: String): String {
        val uri = "disease_vector_${diseaseId.toResourceId()}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchDiseaseCause(diseaseId: String): String {
        val uri = "disease_cause_${diseaseId.toResourceId()}"
        return ResourceUtils.getStringById(context, uri)
    }

    fun fetchDiseaseTreatment(diseaseId: String): List<String> {
        val uri = "string_treatments_${diseaseId.toResourceId()}"
        return ResourceUtils.getStringArrayById(context, uri).toList()
    }

    fun fetchDiseaseSymptoms(diseaseId: String): List<String> {
        val uri = "string_symptoms_${diseaseId.toResourceId()}"
        return ResourceUtils.getStringArrayById(context, uri).toList()
    }

    fun fetchDiseaseCropIds(diseaseId: String): List<String> {
        val uri = "string_crops_${diseaseId.toResourceId()}"
        return ResourceUtils.getStringArrayById(context, uri).toList()
    }

    fun fetchDiseaseCropNames(diseaseId: String): List<String> {
        return mutableListOf<String>().apply {
            fetchDiseaseCropIds(diseaseId).forEach {
                add(fetchCropName(it))
            }
        }
    }

    fun fetchCropSupported(cropId: String): Boolean {
        context.resources.getStringArray(R.array.string_crops).apply {
            return this.contains(cropId)
        }
    }

    fun fetchDiseaseSupported(diseaseId: String): Boolean {
        context.resources.getStringArray(R.array.string_diseases).apply {
            return this.contains(diseaseId)
        }
    }

}