package com.example.cdmdda.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseCompatActivity : AppCompatActivity() {
    private lateinit var currentLang: String

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentLang = LocaleHelper.getLanguage(this@BaseCompatActivity).toString()
    }

    override fun onResume() {
        super.onResume()
        val newLang = LocaleHelper.getLanguage(this@BaseCompatActivity).toString()
        if (currentLang.equals(newLang)) return
        currentLang = newLang
        this@BaseCompatActivity.recreate()
    }

}

