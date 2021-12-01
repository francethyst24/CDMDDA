package com.example.cdmdda.view

import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.cdmdda.R
import com.example.cdmdda.databinding.SettingsActivityBinding
import com.example.cdmdda.model.FirestoreRepository
import com.example.cdmdda.model.SuggestionsProvider
import com.example.cdmdda.view.fragment.ClearDiagnosisDialog
import com.example.cdmdda.view.fragment.ClearSearchDialog

class SettingsActivity : AppCompatActivity(), ClearDiagnosisDialog.ClearDiagnosisDialogListener, ClearSearchDialog.ClearSearchDialogListener {
    companion object { private const val TAG = "SettingsActivity" }
    private lateinit var layout: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = SettingsActivityBinding.inflate(layoutInflater)
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
        android.R.id.home -> { this@SettingsActivity.finish(); true }
        else -> { super.onContextItemSelected(item) }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val clearDiagnosisPreference: Preference? = findPreference("clear_diagnosis")
            val clearSearchPreference: Preference? = findPreference("clear_search")

            clearDiagnosisPreference?.setOnPreferenceClickListener {
                ClearDiagnosisDialog().show(requireActivity().supportFragmentManager, TAG)
                true
            }

            clearSearchPreference?.setOnPreferenceClickListener {
                ClearSearchDialog().show(requireActivity().supportFragmentManager, TAG)
                true
            }
        }

    }


    // TODO progressBar
    override fun onClearDiagnosisClick(fragment: AppCompatDialogFragment) {
        val repository = FirestoreRepository(getString(R.string.dataset))
        repository.deleteAllDiagnosis().addOnCompleteListener{
            if (it.isSuccessful) {
                Toast.makeText(this@SettingsActivity, getString(R.string.text_success_clear_diagnosis), Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onClearSearchClick(fragment: AppCompatDialogFragment) {
        SearchRecentSuggestions(this, SuggestionsProvider.AUTHORITY, SuggestionsProvider.MODE)
            .clearHistory()
    }

}