package com.example.cdmdda.presentation.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.cdmdda.common.ContextUtils.intent
import com.example.cdmdda.presentation.LearnMoreActivity
import com.example.cdmdda.presentation.SettingsActivity
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.DEFAULT_LOCAL
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.DEFAULT_THEME
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.PREF_CLEAR_DIAGNOSIS
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.PREF_CLEAR_SEARCH
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.PREF_LEARN_MORE
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.PREF_PERSONAL
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory

class SettingsFragment constructor(
    private val onThemeChange: (String) -> Unit,
    private val onLocaleChange: (String) -> Unit,
    private val onClearDiagnosisConfirm: (String) -> Unit,
    private val onClearSearchConfirm: (String) -> Unit,
) : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var shared: SharedPreferences? = null
    private val viewModel: SettingsViewModel by activityViewModels {
        CreateWithFactory { SettingsViewModel() }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(viewModel.xmlRoot, rootKey)
        val clearDiagnosis: Preference? = findPreference(PREF_CLEAR_DIAGNOSIS)
        val clearSearch: Preference? = findPreference(PREF_CLEAR_SEARCH)
        val learnMore: Preference? = findPreference(PREF_LEARN_MORE)

        learnMore?.intent = requireActivity().intent(LearnMoreActivity::class.java)

        val personalCategory : PreferenceCategory? = findPreference(PREF_PERSONAL)
        viewModel.user?.let { user ->
            personalCategory?.isVisible = true
            clearDiagnosis?.setOnPreferenceClickListener {
                val dialog = ClearDiagnosisDialog { onClearDiagnosisConfirm(user.uid) }
                dialog.show(requireActivity().supportFragmentManager, SettingsActivity.TAG)
                true
            }

            clearSearch?.setOnPreferenceClickListener {
                val dialog = ClearSearchDialog { onClearSearchConfirm(user.uid) }
                dialog.show(requireActivity().supportFragmentManager, SettingsActivity.TAG)
                true
            }
        }
    }

    override fun onSharedPreferenceChanged(shared: SharedPreferences?, key: String?) {
        when (key) {
            SettingsViewModel.PREF_THEME -> {
                val value = shared?.getString(key, DEFAULT_THEME).toString()
                onThemeChange(value)
            }
            SettingsViewModel.PREF_LOCAL -> {
                val value = shared?.getString(key, DEFAULT_LOCAL).toString()
                onLocaleChange(value)
            }
        }
    }

    // region // lifecycle
    override fun onResume() {
        super.onResume()
        shared?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        shared?.unregisterOnSharedPreferenceChangeListener(this)
    }
    // endregion

    // interact: parent
    override fun onAttach(context: Context) {
        super.onAttach(context)
        shared = PreferenceManager.getDefaultSharedPreferences(context as SettingsActivity)
        shared?.registerOnSharedPreferenceChangeListener(this)
    }

}