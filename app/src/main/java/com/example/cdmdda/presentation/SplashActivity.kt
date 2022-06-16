package com.example.cdmdda.presentation

import android.os.Bundle
import androidx.preference.PreferenceManager
import com.example.cdmdda.common.Constants.ON_INITIAL
import com.example.cdmdda.common.utils.AndroidUtils.intent

class SplashActivity : BaseCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = PreferenceManager
            .getDefaultSharedPreferences(applicationContext)
        val decision = sharedPref.getBoolean(ON_INITIAL, false)
        val destination = if (decision) {
            MainActivity::class.java
        } else {
            OnboardingActivity::class.java
        }
        startActivity(intent(destination))
        finish()
    }
}