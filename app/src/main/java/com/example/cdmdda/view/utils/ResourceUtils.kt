package com.example.cdmdda.view.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

object ResourceUtils {
    private const val RESOURCE_STRING = "string"
    private const val RESOURCE_ARRAY = "array"
    private const val RESOURCE_DRAWABLE = "drawable"

    fun getStringById(context: Context, resId: String): String {
        val id = context.resources.getIdentifier(
            ("@${RESOURCE_STRING}/").plus(resId),
            RESOURCE_STRING,
            context.packageName
        )
        return try {
            context.resources.getString(id)
        } catch (e: Resources.NotFoundException) {
            String()
        }
    }

    fun getStringArrayById(context: Context, resId: String): Array<out String> {
        val id = context.resources.getIdentifier(
            ("@${RESOURCE_ARRAY}/").plus(resId),
            RESOURCE_ARRAY,
            context.packageName
        )
        return try {
            context.resources.getStringArray(id)
        } catch (e: Resources.NotFoundException) {
            arrayOf()
        }
    }

    fun getDrawableById(context: Context, resId: String): Drawable {
        val id = context.resources.getIdentifier(
            ("@${RESOURCE_DRAWABLE}/").plus(resId),
            RESOURCE_DRAWABLE,
            context.packageName
        )
        return try {
            context.resources.getDrawable(id, null)
        } catch (e: Resources.NotFoundException) {
            ColorDrawable(Color.TRANSPARENT)
        }
    }

}