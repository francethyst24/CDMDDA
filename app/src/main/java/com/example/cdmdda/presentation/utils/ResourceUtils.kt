package com.example.cdmdda.presentation.utils

import android.content.Context
import android.content.res.Resources

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

    fun getDrawableId(context: Context, resId: String): Int {
        return context.resources.getIdentifier(
            ("@${RESOURCE_DRAWABLE}/").plus(resId),
            RESOURCE_DRAWABLE,
            context.packageName
        )
    }

    fun getDrawableArrayId(context: Context, resId: String) : Int {
        return context.resources.getIdentifier(
            ("@${RESOURCE_ARRAY}/").plus(resId),
            RESOURCE_ARRAY,
            context.packageName
        )
    }

}