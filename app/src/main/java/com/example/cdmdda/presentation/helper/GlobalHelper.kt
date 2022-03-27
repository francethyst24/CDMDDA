package com.example.cdmdda.presentation.helper

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.example.cdmdda.R

class GlobalHelper(private val applicationContext: Context) {
    companion object {
        const val TAG = "GlobalHelper"
        const val DIM = 224
    }

    fun tfliteLabels(): Array<String> = try {
        applicationContext.resources.getStringArray(R.array.tflite_labels)
    } catch (e: Exception) {
        Log.w(TAG, "No values yielded for tfliteLabels: Array<String>")
        arrayOf()
    }

    fun dataset() = try {
        applicationContext.resources.getString(R.string.var_dataset)
    } catch (e: Exception) {
        Log.w(TAG, "No value yielded for dataset: String")
        String()
    }

    fun allDiseases(): Array<String> = try {
        applicationContext.resources.getStringArray(R.array.string_unsupported_diseases)
    } catch (e: Exception) {
        Log.w(TAG, "No values yielded for allDiseases: Array<String>")
        arrayOf()
    }

    fun supportedDiseases(): Array<String> = try {
        applicationContext.resources.getStringArray(R.array.string_diseases)
    } catch (e: Resources.NotFoundException) {
        Log.w(TAG, "No values yielded for supportedDiseases: Array<String>")
        arrayOf()
    }

    fun allCrops(): Array<String> = try {
        applicationContext.resources.getStringArray(R.array.string_unsupported_crops)
    } catch (e: Exception) {
        Log.w(TAG, "No values yielded for allCrops: Array<String>")
        arrayOf()
    }

    fun supportedCrops(): Array<String> = try {
        applicationContext.resources.getStringArray(R.array.string_crops)
    } catch (e: Resources.NotFoundException) {
        Log.w(TAG, "No values yielded for supportedCrops: Array<String>")
        arrayOf()
    }
}