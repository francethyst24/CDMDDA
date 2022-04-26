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
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.helper.ThemeHelper
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel
import com.example.cdmdda.presentation.viewmodel.factory.viewModelBuilder

class SettingsActivity : BaseCompatActivity(), OnLogoutListener {
    companion object {
        const val TAG = "SettingsActivity"
    }

    private val layout by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val viewModel by viewModelBuilder {
        SettingsViewModel(
            ThemeHelper.getTheme(this),
            LocaleHelper.getLocale(this).toString(),
        )
    }
    private val settingsFragment by lazy { SettingsFragment() }

    override fun logout() {
        viewModel.signOut(this)
        toast(viewModel.uiWarnLogout)
        restartMain()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarSettings)
        supportActionBar?.title = getString(viewModel.uiHeadSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        savedInstanceState ?: supportFragmentManager.beginTransaction()
            .replace(viewModel.uiSettings, settingsFragment)
            .commit()

        viewModel.isClearDiagnosisConfirmed.observe(this) {
            if (it != null) {
                clearDiagnosis(it)
                viewModel.confirmClearDiagnosis(null)
            }
        }
        viewModel.isClearSearchConfirmed.observe(this) {
            if (it != null) {
                clearSearch(it)
                viewModel.confirmClearSearch(null)
            }
        }
        viewModel.isThemeChangeConfirmed.observe(this) {
            if (it != ThemeHelper.getTheme(this)) changeTheme(it)
        }

        viewModel.isLocalChangeConfirmed.observe(this) {
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
        viewModel.clearDiagnosis(uid)
        toast(viewModel.uiInfoClearDiagnosisSuccess)
        toggleLoadingUI()
    }

    private fun clearSearch(uid: String) {
        toggleLoadingUI()
        viewModel.clearSearch(this, uid)
        toast(viewModel.uiInfoClearSearchSuccess)
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

}

