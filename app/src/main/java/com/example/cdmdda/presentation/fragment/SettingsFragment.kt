package com.example.cdmdda.presentation.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.cdmdda.common.utils.AndroidUtils.intent
import com.example.cdmdda.presentation.LearnMoreActivity
import com.example.cdmdda.presentation.SettingsActivity
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.DEFAULT_LOCALE
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.DEFAULT_THEME
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.PREF_CLEAR_DIAGNOSIS
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.PREF_CLEAR_SEARCH_QUERY
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.PREF_LEARN_MORE
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.PREF_LOGOUT
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel.Companion.PREF_PERSONAL
import com.example.cdmdda.presentation.viewmodel.factory.activityViewModelBuilder

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var shared: SharedPreferences? = null
    private val viewModel by activityViewModelBuilder {
        SettingsViewModel(
            LocaleHelper.getLocale(this).toString(),
        )
    }

    interface OnSettingChangeListener {
        fun onThemeChanged(newTheme: String)
    }

    private var listener: OnSettingChangeListener? = null


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) = with(requireActivity()) {
        setPreferencesFromResource(viewModel.xmlRoot, rootKey)
        val clearDiagnosisPreference: Preference? = findPreference(PREF_CLEAR_DIAGNOSIS)
        val clearSearchQueryPreference: Preference? = findPreference(PREF_CLEAR_SEARCH_QUERY)
        val logoutPreference: Preference? = findPreference(PREF_LOGOUT)
        val learnMorePreference: Preference? = findPreference(PREF_LEARN_MORE)

        if (viewModel.isLoggedIn) {
            val personalCategory: PreferenceCategory? = findPreference(PREF_PERSONAL)
            personalCategory?.isVisible = true

            clearDiagnosisPreference?.setOnPreferenceClickListener {
                ClearDiagnosisDialog().show(supportFragmentManager, ClearDiagnosisDialog.TAG)
                true
            }

            clearSearchQueryPreference?.setOnPreferenceClickListener {
                ClearSearchQueryDialog().show(supportFragmentManager, ClearSearchQueryDialog.TAG)
                true
            }

            logoutPreference?.setOnPreferenceClickListener {
                LogoutDialog().show(supportFragmentManager, LogoutDialog.TAG)
                true
            }
        }
        learnMorePreference?.intent = requireActivity().intent(LearnMoreActivity::class.java)
    }

    override fun onSharedPreferenceChanged(shared: SharedPreferences?, key: String?) {
        when (key) {
            SettingsViewModel.PREF_THEME -> {
                val value = shared?.getString(key, DEFAULT_THEME).toString()
                /*viewModel.confirmThemeChange(value)*/
                listener?.onThemeChanged(value)
            }
            SettingsViewModel.PREF_LOCAL -> {
                val value = shared?.getString(key, DEFAULT_LOCALE).toString()
                viewModel.confirmLocalChange(value)
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
        listener = context as OnSettingChangeListener
        shared = PreferenceManager.getDefaultSharedPreferences(context as SettingsActivity)
        shared?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

}