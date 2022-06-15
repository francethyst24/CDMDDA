package com.example.cdmdda.common

import com.example.cdmdda.R
import com.google.firebase.firestore.Query

object Constants {
    const val ON_INITIAL = "on_initial"

    const val LABELS = R.array.pytorch_labels // R.array.tflite_labels
    const val DATASET = "set1"

    const val SUPPORTED_CROPS = R.array.string_crops
    const val ALL_CROPS = R.array.string_unsupported_crops
    const val SUPPORTED_DISEASES = R.array.string_diseases
    const val ALL_DISEASES = R.array.string_unsupported_diseases

    // ResourceType
    const val STRING = "string"
    const val ARRAY = "array"
    const val DRAWABLE = "drawable"

    // region // ResourcePrefix
    const val CROP_NAME = "crop_name_"
    const val DESC = "crop_desc_"
    const val SCI_NAME = "crop_sci_name_"
    const val DISEASES_ACQUIRABLE = "diseases_acquirable_"
    const val BANNER = "crop_banner_"
    const val DISEASE_NAME = "disease_name_"
    const val VECTOR = "disease_vector_"
    const val CAUSE = "disease_cause_"
    const val TREATMENTS = "disease_treatments_"
    const val SYMPTOMS = "disease_symptoms_"
    const val CROPS_AFFECTED = "crops_affected_"
    const val OFFLINE_IMAGES = "drawables_"
    // endregion

    // Intent Extras
    const val INIT_QUERIES = "init_queries"
    const val CROP = "crop_id"
    const val DISEASE = "disease_id"
    const val PARENT_ACTIVITY = "parent_activity"

    // DataType
    const val NULL = "null"
    const val OUT_OF_BOUNDS = -1
    const val TFLITE_INFERRED_VALUE = 1.0f
    const val DIM = 224

    // Inference
    const val HEALTHY = "Healthy"
    val FAILED_VALUES = hashSetOf(NULL, HEALTHY)

    // Assets
    const val MODEL = "model.ptl"

    // Query Order
    val DESCENDING by lazy { Query.Direction.DESCENDING }
}