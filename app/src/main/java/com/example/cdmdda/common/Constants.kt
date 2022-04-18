package com.example.cdmdda.common

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

object Constants {
    // Intent Extras
    const val INIT_QUERIES = "init_queries"
    const val CROP = "crop_id"
    const val DISEASE = "disease_id"
    // Intent Flags
    const val FLAG_ACTIVITY_CLEAR_TOP = Intent.FLAG_ACTIVITY_CLEAR_TOP

    // PackageManager
    const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED

    // LinearLayoutManager
    const val ORIENTATION_Y = LinearLayoutManager.VERTICAL
    const val ORIENTATION_X = LinearLayoutManager.HORIZONTAL

    // Configuration
    const val UI_MODE_NIGHT_MASK = Configuration.UI_MODE_NIGHT_MASK
    const val UI_MODE_NIGHT_YES = Configuration.UI_MODE_NIGHT_YES

    // MenuItem
    const val SHOW_AS_ACTION_NEVER = MenuItem.SHOW_AS_ACTION_NEVER

    // Toast
    const val TOAST_SHORT = Toast.LENGTH_SHORT

    // DataType
    const val NULL = "null"
    const val OUT_OF_BOUNDS = -1
    const val TFLITE_INFERRED_VALUE = 1.0f

    // ActivityResult
    const val INPUT_IMAGE = "image/*"

    // Inference
    const val HEALTHY = "Healthy"

    // Firebase
    const val WRITE_SUCCESS = "Firebase Write Success"
    const val WRITE_FAILURE = "Firebase Write Failure"

    // Assets
    const val MODEL = "model.ptl"

    // Regex
    const val CAPITAL = "[A-Z]"
    const val NUMERAL = "[0-9]"
    // const val SPECIAL = "[^a-zA-Z0-9 ]"
    const val WHITESPACE = " "
}