package com.example.cdmdda.view

import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivitySettingsBinding
import com.example.cdmdda.model.FirestoreRepository
import com.example.cdmdda.model.SuggestionsProvider
import com.example.cdmdda.view.fragment.ClearDiagnosisDialog
import com.example.cdmdda.view.fragment.ClearSearchDialog
import com.example.cdmdda.view.fragment.SettingsFragment
import com.example.cdmdda.view.utils.LocaleUtils

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
        supportActionBar?.title = getString(R.string.action_settings)
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
        val repository = FirestoreRepository(getString(R.string.dataset))
        repository.deleteAllDiagnosis().addOnCompleteListener {
            if (it.isSuccessful) {
                updateUIOnClearing(View.INVISIBLE)
                Toast.makeText(
                    this@SettingsActivity,
                    getString(R.string.text_success_clear_diagnosis),
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
        "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun onLanguageChanged(langValue: String) {
        LocaleUtils.setLocale(this@SettingsActivity, langValue)
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

