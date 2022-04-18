package com.example.cdmdda.data.repository

import android.content.Context
import android.content.res.Resources
import com.example.cdmdda.R
import com.example.cdmdda.common.StringArray
import com.example.cdmdda.common.StringUtils.snakecase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class DataRepository(
    protected val context: Context,
    protected val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    protected val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    companion object {
        const val TAG = "DataRepository"
        const val DIM = 224

        // ResourceType
        const val STRING = "string"
        const val ARRAY = "array"
        const val DRAWABLE = "drawable"

        // ResourcePrefix
        const val NAME = "crop_name_"
        const val DESC = "crop_desc_"
        const val SCI_NAME = "crop_sci_name_"
        const val DISEASES_ACQUIRABLE = "diseases_acquirable_"
        const val BANNER = "crop_banner_"
        const val VECTOR = "disease_vector_"
        const val CAUSE = "disease_cause_"
        const val TREATMENTS = "disease_treatments_"
        const val SYMPTOMS = "disease_symptoms_"
        const val CROPS_AFFECTED = "crops_affected_"
        const val OFFLINE_IMAGES = "drawables_"
    }

    protected fun getResourceId(name: String, type: String): Int = context.run {
        return resources.getIdentifier("@$type/$name", type, packageName)
    }

    protected suspend fun getStringArrayBy(id: String): StringArray = withContext(ioDispatcher) {
        val resId = context.resources.getIdentifier(("@$ARRAY/$id"), ARRAY, context.packageName)
        return@withContext try {
            context.resources.getStringArray(resId)
        } catch (e: Resources.NotFoundException) {
            arrayOf()
        }
    }

    private fun getStringArray(id: Int): StringArray {
        return try {
            context.resources.getStringArray(id)
        } catch (e: Resources.NotFoundException) {
            arrayOf()
        }
    }

    private fun getString(id: Int): String = try {
        context.resources.getString(id)
    } catch (e: Resources.NotFoundException) {
        String()
    }

    suspend fun name(id: String) = withContext(ioDispatcher) {
        return@withContext getResourceId("$NAME${id.snakecase}", STRING)
    }

    val dataset: String by lazy { getString(R.string.var_dataset) }
    //val tfliteLabels: StringArray by lazy { getStringArray(R.array.tflite_labels) }
    // TODO: remove tfliteLabels after migration
    val pytorchLabels: StringArray by lazy { getStringArray(R.array.pytorch_labels) }
    val allDiseases: StringArray by lazy { getStringArray(R.array.string_unsupported_diseases) }
    val supportedDiseases: StringArray by lazy { getStringArray(R.array.string_diseases) }
    val allCrops: StringArray by lazy { getStringArray(R.array.string_unsupported_crops) }
    val supportedCrops: StringArray by lazy { getStringArray(R.array.string_crops) }
}