package com.example.cdmdda.presentation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        currentTheme = ThemeHelper.getTheme(this)
        currentLang = LocaleHelper.getLocale(this).toString()
    }

    override fun onResume() {
        super.onResume()
        val newTheme = ThemeHelper.getTheme(this)
        if (currentTheme != newTheme) currentTheme = newTheme

        val newLang = LocaleHelper.getLocale(this).toString()
        if (currentLang != newLang) {
            currentLang = newLang
            recreate()
        }
    }

}

