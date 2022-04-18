package com.example.cdmdda.presentation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.helper.ThemeHelper

open class BaseCompatActivity : AppCompatActivity() {
    /*companion object {
        const val DELETE_SEARCH_PREF = "search_delete"
        const val DELETE_DIAGNOSIS_PREF = "diagnosis_delete"
    }*/
    private lateinit var currentLang: String
    private lateinit var currentTheme: String

    /*private val preferences: SharedPreferences = getPreferences(Context.MODE_PRIVATE)
    var isDeletingSearch: Boolean
        get() = preferences.getBoolean(DELETE_SEARCH_PREF, false)
        set(value) = preferences.edit().putBoolean(DELETE_SEARCH_PREF, false).apply()
    var isDeletingDiagnosis: Boolean
        get() = preferences.getBoolean(DELETE_DIAGNOSIS_PREF, false)
        set(value) = preferences.edit().putBoolean(DELETE_DIAGNOSIS_PREF, false).apply()*/

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

}

