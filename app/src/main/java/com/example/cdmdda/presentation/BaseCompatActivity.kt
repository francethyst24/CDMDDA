package com.example.cdmdda.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.cdmdda.common.AppData
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.helper.ThemeHelper

open class BaseCompatActivity : AppCompatActivity() {
    private lateinit var currentLang: String
    private lateinit var currentTheme: String

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
        ThemeHelper.onAttach(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTheme = ThemeHelper.getTheme(this).toString()
        currentLang = LocaleHelper.getLanguage(this).toString()
    }

    override fun onResume() {
        super.onResume()
        val newTheme = ThemeHelper.getTheme(this).toString()
        if (currentTheme != newTheme) currentTheme = newTheme

        val newLang = LocaleHelper.getLanguage(this).toString()
        if (currentLang == newLang) return
        currentLang = newLang
        recreate()
    }

    protected fun Context.interactivity(type: String? = null, parcel: Parcelable? = null): Intent = when(type) {
        AppData.CROP -> Intent(this, CropProfileActivity::class.java).putExtra(type, parcel)
        AppData.DISEASE -> Intent(this, DiseaseProfileActivity::class.java).putExtra(type, parcel)
        else -> (this as Activity).intent
    }

}

