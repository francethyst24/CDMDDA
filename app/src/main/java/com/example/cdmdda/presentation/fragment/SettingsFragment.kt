package com.example.cdmdda.presentation.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.cdmdda.R
import com.example.cdmdda.domain.usecase.GetAuthStateUseCase
import com.example.cdmdda.presentation.LearnMoreActivity
import com.example.cdmdda.presentation.SettingsActivity
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory

class SettingsFragment(
    private val onThemePreferenceChange: (String) -> Unit,
    private val onLocalPreferenceChange: (String) -> Unit,
    private val onClearDiagnosisConfirm: (String) -> Unit,
    private val onClearSearchConfirm: (String) -> Unit,
) : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var sharedPreferences: SharedPreferences? = null
    private val model: SettingsViewModel by activityViewModels {
        CreateWithFactory { SettingsViewModel(GetAuthStateUseCase()) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val clearDiagnosisPreference: Preference? = findPreference(SettingsViewModel.PREF_CLEAR_DIAGNOSIS)
        val clearSearchPreference: Preference? = findPreference(SettingsViewModel.PREF_CLEAR_SEARCH)
        val learnMorePreference: Preference? = findPreference(SettingsViewModel.PREF_LEARN_MORE)

        learnMorePreference?.intent = Intent(context, LearnMoreActivity::class.java)

        val personalPreferenceCategory : PreferenceCategory? = findPreference(SettingsViewModel.PREF_PERSONAL)
        model.user?.let { user ->
            personalPreferenceCategory?.isVisible = true
            clearDiagnosisPreference?.setOnPreferenceClickListener {
                val dialog = ClearDiagnosisDialog { onClearDiagnosisConfirm(user.uid) }
                dialog.show(requireActivity().supportFragmentManager, SettingsActivity.TAG)
                true
            }

            clearSearchPreference?.setOnPreferenceClickListener {
                val dialog = ClearSearchDialog { onClearSearchConfirm(user.uid) }
                dialog.show(requireActivity().supportFragmentManager, SettingsActivity.TAG)
                true
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            SettingsViewModel.PREF_THEME -> {
                val value = sharedPreferences?.getString(key, SettingsViewModel.DEFAULT_THEME).toString()
                onThemePreferenceChange(value)
            }
            SettingsViewModel.PREF_LANG -> {
                val value = sharedPreferences?.getString(key, SettingsViewModel.DEFAULT_LANG).toString()
                onLocalPreferenceChange(value)
            }
        }
    }

    // region // lifecycle
    override fun onResume() {
        super.onResume()
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }
    // endregion

    // region // interact: parent
    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context as SettingsActivity)
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }
    // endregion

}