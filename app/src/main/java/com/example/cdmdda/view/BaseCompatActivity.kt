package com.example.cdmdda.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.cdmdda.view.fragment.SettingsFragment
import com.example.cdmdda.view.utils.LocaleUtils
import com.example.cdmdda.view.utils.ThemeUtils

open class BaseCompatActivity : AppCompatActivity() {
    private lateinit var currentLang: String
    private lateinit var currentTheme: String

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.onAttach(newBase))
        ThemeUtils.onAttach(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTheme = ThemeUtils.getTheme(this@BaseCompatActivity).toString()
        currentLang = LocaleUtils.getLanguage(this@BaseCompatActivity).toString()
    }

    override fun onResume() {
        super.onResume()
        val newTheme = ThemeUtils.getTheme(this@BaseCompatActivity).toString()
        if (currentTheme != newTheme) {
            currentTheme = newTheme
        }

        val newLang = LocaleUtils.getLanguage(this@BaseCompatActivity).toString()
        if (currentLang == newLang) return
        currentLang = newLang
        this@BaseCompatActivity.recreate()
    }

}

