package com.example.cdmdda.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cdmdda.view.utils.LocaleUtils

open class BaseCompatActivity : AppCompatActivity() {
    private lateinit var currentLang: String

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentLang = LocaleUtils.getLanguage(this@BaseCompatActivity).toString()
    }

    override fun onResume() {
        super.onResume()
        val newLang = LocaleUtils.getLanguage(this@BaseCompatActivity).toString()
        if (currentLang == newLang) return
        currentLang = newLang
        this@BaseCompatActivity.recreate()
    }

}

