package com.example.cdmdda.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.preference.*
import com.example.cdmdda.R
import com.example.cdmdda.view.SettingsActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object { const val TAG = "SettingsFragment" }
    private lateinit var sharedPreferences: SharedPreferences
    var appearancePreferenceCategory: PreferenceCategory? = null
    var languagePreferenceCategory: PreferenceCategory? = null
    var personalPreferenceCategory: PreferenceCategory? = null
    var languageListPreference: ListPreference? = null
    var clearDiagnosisPreference: Preference? = null
    var clearSearchPreference: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        languageListPreference = findPreference("lang")
        clearDiagnosisPreference = findPreference("clear_diagnosis")
        clearSearchPreference = findPreference("clear_search")

        clearDiagnosisPreference?.setOnPreferenceClickListener {
            ClearDiagnosisDialog().show(requireActivity().supportFragmentManager, SettingsActivity.TAG)
            true
        }

        clearSearchPreference?.setOnPreferenceClickListener {
            ClearSearchDialog().show(requireActivity().supportFragmentManager, SettingsActivity.TAG)
            true
        }

        appearancePreferenceCategory = findPreference("appearance")
        languagePreferenceCategory = findPreference("language")
        val auth = Firebase.auth
        personalPreferenceCategory = findPreference("personal")
        personalPreferenceCategory?.isVisible = auth.currentUser != null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "theme" -> {
                val themeValue = sharedPreferences?.getString(key, "default").toString()
                settingsFragmentListener.onThemeChanged(themeValue)
            }
            "lang" -> {
                val langValue = sharedPreferences?.getString(key, "en").toString()
                settingsFragmentListener.onLanguageChanged(langValue, this@SettingsFragment)
            }
        }
    }

    // region // lifecycle
    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
    // endregion

    // region // interact: parent
    private lateinit var settingsFragmentListener: SettingsFragmentListener

    interface SettingsFragmentListener {
        fun onThemeChanged(themeValue: String)
        fun onLanguageChanged(langValue: String, fragment: SettingsFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context as SettingsActivity)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        settingsFragmentListener = context
    }
    // endregion


}