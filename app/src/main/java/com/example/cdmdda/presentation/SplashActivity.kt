package com.example.cdmdda.presentation

import android.content.Intent
import android.os.Bundle

class SplashActivity : BaseCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}