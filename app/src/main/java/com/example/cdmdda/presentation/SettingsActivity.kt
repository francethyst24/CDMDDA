package com.example.cdmdda.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.example.cdmdda.common.utils.AndroidUtils.restartCurrent
import com.example.cdmdda.common.utils.AndroidUtils.restartMain
import com.example.cdmdda.common.utils.AndroidUtils.toast
import com.example.cdmdda.databinding.ActivitySettingsBinding
import com.example.cdmdda.presentation.fragment.LogoutDialog.OnLogoutListener
import com.example.cdmdda.presentation.fragment.SettingsFragment
import com.example.cdmdda.presentation.fragment.SettingsFragment.OnSettingChangeListener
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.helper.ThemeHelper
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel
import com.example.cdmdda.presentation.viewmodel.LogoutDialogViewModel
import com.example.cdmdda.presentation.viewmodel.factory.viewModelBuilder

class SettingsActivity : BaseCompatActivity(), OnLogoutListener, OnSettingChangeListener {
    companion object {
        const val TAG = "SettingsActivity"
    }

    private val layout by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val model by viewModelBuilder {
        SettingsViewModel(
            LocaleHelper.getLocale(this).toString(),
        )
    }
    private val logoutDialogModel by viewModelBuilder {
        LogoutDialogViewModel()
    }
    private val settingsFragment by lazy { SettingsFragment() }

    override fun logout() {
        logoutDialogModel.signOut(this)
        toast(logoutDialogModel.uiWarnLogout)
        restartMain()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarSettings)
        supportActionBar?.title = getString(model.uiHeadSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        savedInstanceState ?: supportFragmentManager.beginTransaction()
            .replace(model.uiSettings, settingsFragment)
            .commit()

        model.isClearDiagnosisConfirmed.observe(this) {
            if (it != null) {
                clearDiagnosis(it)
                model.confirmClearDiagnosis(null)
            }
        }
        model.isClearSearchConfirmed.observe(this) {
            if (it != null) {
                clearSearch(it)
                model.confirmClearSearch(null)
            }
        }

        model.isLocalChangeConfirmed.observe(this) {
            if (it != LocaleHelper.getLocale(this)) changeLocal(it)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onContextItemSelected(item)
    }

    private fun clearDiagnosis(uid: String) {
        toggleLoadingUI()
        model.clearDiagnosis(uid)
        toast(model.uiInfoClearDiagnosisSuccess)
        toggleLoadingUI()
    }

    private fun clearSearch(uid: String) {
        toggleLoadingUI()
        model.clearSearch(this, uid)
        toast(model.uiInfoClearSearchSuccess)
        toggleLoadingUI()
    }

    private fun changeTheme(themeValue: String) {
        ThemeHelper.setTheme(this, themeValue)
        restartCurrent()
    }

    private fun changeLocal(langValue: String) {
        LocaleHelper.setLocale(this, langValue)
        restartCurrent()
    }

    private fun toggleLoadingUI() = with(layout) {
        if (maskSettings.visibility == View.VISIBLE) {
            loadingClearing.visibility = View.GONE
            maskSettings.visibility = View.GONE
        } else {
            loadingClearing.visibility = View.VISIBLE
            maskSettings.visibility = View.VISIBLE
        }
    }

    override fun onThemeChanged(newTheme: String) {
        changeTheme(newTheme)
    }

}

