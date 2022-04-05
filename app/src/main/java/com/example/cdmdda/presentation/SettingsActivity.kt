package com.example.cdmdda.presentation

import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.cdmdda.R
import com.example.cdmdda.data.SuggestionsProvider
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.databinding.ActivitySettingsBinding
import com.example.cdmdda.domain.usecase.GetAuthStateUseCase
import com.example.cdmdda.presentation.fragment.SettingsFragment
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.helper.ThemeHelper
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory

class SettingsActivity : BaseCompatActivity() {
    companion object { const val TAG = "SettingsActivity" }

    private val layout: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }
    private val viewModel: SettingsViewModel by viewModels {
        CreateWithFactory { SettingsViewModel(GetAuthStateUseCase()) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarSettings)
        supportActionBar?.title = getString(R.string.ui_text_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        val settingsFragment = SettingsFragment(
            onThemePreferenceChange = { onThemeChanged(it) },
            onLocalPreferenceChange = { onLanguageChanged(it) },
            onClearDiagnosisConfirm = { onClearDiagnosisClick(it) },
            onClearSearchConfirm = { onClearSearchClick(it) },
        )
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.settings, settingsFragment).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onContextItemSelected(item)
    }

    private fun onClearDiagnosisClick(uid: String) {
        updateUIOnClearing(View.VISIBLE)
        viewModel.clearDiagnosis(DiagnosisRepository(uid)) {
            Toast.makeText(this, getString(R.string.ui_text_clear_diagnosis_success), Toast.LENGTH_SHORT) .show()
        }
        updateUIOnClearing(View.INVISIBLE)
    }

    private fun onClearSearchClick(uid: String) {
        updateUIOnClearing(View.VISIBLE)
        val repository = SearchQueryRepository(uid)

        val auth = SuggestionsProvider.AUTHORITY
        val mode = SuggestionsProvider.MODE
        val provider = SearchRecentSuggestions(this, auth, mode)
        viewModel.clearSearch(repository, provider) {
            Toast.makeText(this, getString(R.string.ui_text_clear_search_success), Toast.LENGTH_SHORT).show()
        }
        updateUIOnClearing(View.INVISIBLE)
    }

    private fun onThemeChanged(themeValue: String) {
        ThemeHelper.setTheme(this, themeValue)
        restartActivityStack()
    }

    private fun onLanguageChanged(langValue: String) {
        LocaleHelper.setLocale(this, langValue)
        restartActivityStack()
    }

    private fun restartActivityStack() {
        finishAffinity()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        startActivity(interactivity())
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

