package com.example.cdmdda.view.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.cdmdda.view.DisplayCropActivity
import com.example.cdmdda.view.DisplayDiseaseActivity

object IntentUtils {
    fun interactivityIntent(context: Context, id: String) : Intent = when (context) {
        is DisplayDiseaseActivity ->
            Intent(context, DisplayCropActivity::class.java).putExtra("crop_id", id)
        is DisplayCropActivity ->
            Intent(context, DisplayDiseaseActivity::class.java).putExtra("disease_id", id)
        else ->
            (context as Activity).intent
    }
}