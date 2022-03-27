package com.example.cdmdda.presentation.helper

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

object ThemeHelper {
    private const val SELECTED_THEME = "Theme.Helper.Selected.Theme"

    private fun defaultTheme(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences?.getString("theme", "default").toString()
    }

    fun onAttach(context: Context) {
        val theme = getPersistedData(context, defaultTheme(context))
        setTheme(context, theme)
    }

    fun onAttach(context: Context, defaultTheme: String) {
        val theme = getPersistedData(context, defaultTheme)
        setTheme(context, theme)
    }

    fun getTheme(context: Context): String? {
        return getPersistedData(context, defaultTheme(context))
    }

    private fun getPersistedData(context: Context, defaultTheme: String): String? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(SELECTED_THEME, defaultTheme)
    }

    fun setTheme(context: Context, theme: String?) {
        persist(context, theme)
        when (theme) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun persist(context: Context, theme: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(SELECTED_THEME, theme)
        editor.apply()
    }
}