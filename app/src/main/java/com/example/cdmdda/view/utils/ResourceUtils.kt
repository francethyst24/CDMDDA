package com.example.cdmdda.view.utils

import android.content.Context
import android.graphics.drawable.Drawable

object ResourceUtils {
    private const val RESOURCE_STRING = "string"
    private const val RESOURCE_DRAWABLE = "drawable"

    fun getStringById(context: Context, resId: String): String {
        val id = context.resources.getIdentifier(resId, RESOURCE_STRING, context.packageName)
        return context.resources.getString(id)
    }

    fun getDrawableById(context: Context, resId: String): Drawable {
        val id = context.resources.getIdentifier(resId, RESOURCE_DRAWABLE, context.packageName)
        return context.resources.getDrawable(id, null)
    }

}