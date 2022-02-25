package com.example.cdmdda.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.*
import com.example.cdmdda.R
import com.example.cdmdda.view.SettingsActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val clearDiagnosisPreference: Preference? = findPreference("clear_diagnosis")
        val clearSearchPreference: Preference? = findPreference("clear_search")

        clearDiagnosisPreference?.setOnPreferenceClickListener {
            ClearDiagnosisDialog().show(requireActivity().supportFragmentManager, SettingsActivity.TAG)
            true
        }

        clearSearchPreference?.setOnPreferenceClickListener {
            ClearSearchDialog().show(requireActivity().supportFragmentManager, SettingsActivity.TAG)
            true
        }
        // val auth = Firebase.auth
        val personalPreferenceCategory : PreferenceCategory? = findPreference("personal")
        // personalPreferenceCategory?.isVisible = auth.currentUser != null
        Firebase.auth.currentUser.let {
            personalPreferenceCategory?.isVisible = true
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "theme" -> {
                val themeValue = sharedPreferences?.getString(key, "default").toString()
                settingsFragmentListener.onThemeChanged(themeValue)
            }
            "lang" -> {
                val langValue = sharedPreferences?.getString(key, "en").toString()
                settingsFragmentListener.onLanguageChanged(langValue)
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
        fun onLanguageChanged(langValue: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context as SettingsActivity)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        settingsFragmentListener = context
    }
    // endregion


}