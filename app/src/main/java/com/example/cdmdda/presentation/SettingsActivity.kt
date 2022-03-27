package com.example.cdmdda.presentation

import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.cdmdda.R
import com.example.cdmdda.data.SuggestionsProvider
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.databinding.ActivitySettingsBinding
import com.example.cdmdda.presentation.fragment.ClearDiagnosisDialog
import com.example.cdmdda.presentation.fragment.ClearSearchDialog
import com.example.cdmdda.presentation.fragment.SettingsFragment
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.helper.ThemeHelper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingsActivity : BaseCompatActivity(), ClearDiagnosisDialog.ClearDiagnosisDialogListener,
    ClearSearchDialog.ClearSearchDialogListener, SettingsFragment.SettingsFragmentListener {
    companion object {
        const val TAG = "SettingsActivity"
    }

    private lateinit var layout: ActivitySettingsBinding
    //private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@SettingsActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivitySettingsBinding.inflate(layoutInflater)
        setSupportActionBar(layout.toolbarSettings)
        supportActionBar?.title = getString(R.string.ui_text_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@SettingsActivity.finish(); true
        }
        else -> {
            super.onContextItemSelected(item)
        }
    }

    override fun onClearDiagnosisClick() {
        updateUIOnClearing(View.VISIBLE)
        val repository = DiagnosisRepository(Firebase.firestore, Firebase.auth.uid.toString())
        repository.deleteAllDiagnosis().addOnCompleteListener {
            if (it.isSuccessful) {
                updateUIOnClearing(View.INVISIBLE)
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.ui_text_clear_diagnosis_success),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onClearSearchClick() {
        updateUIOnClearing(View.VISIBLE)
        SearchRecentSuggestions(this@SettingsActivity, SuggestionsProvider.AUTHORITY, SuggestionsProvider.MODE)
            .clearHistory()
        updateUIOnClearing(View.INVISIBLE)
    }

    override fun onThemeChanged(themeValue: String) = when (themeValue) {
        "dark" -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        "light" -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        else -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }.also { ThemeHelper.setTheme(this@SettingsActivity, themeValue) }

    override fun onLanguageChanged(langValue: String) {
        LocaleHelper.setLocale(this@SettingsActivity, langValue)
        finishAffinity()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        startActivity(Intent(intent))
    }

    private fun updateUIOnClearing(visibility: Int) = when(visibility) {
        View.GONE, View.INVISIBLE -> {
            layout.apply {
                loadingClearing.visibility = View.GONE
                maskSettings.visibility = View.GONE
            }
        }
        else -> {
            layout.apply {
                loadingClearing.visibility = View.VISIBLE
                maskSettings.visibility = View.VISIBLE
            }
        }
    }

}

