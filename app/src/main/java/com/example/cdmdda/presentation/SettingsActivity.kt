package com.example.cdmdda.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.example.cdmdda.common.ContextUtils.intent
import com.example.cdmdda.common.ContextUtils.intentWith
import com.example.cdmdda.common.ContextUtils.toast
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.databinding.ActivitySettingsBinding
import com.example.cdmdda.presentation.fragment.SettingsFragment
import com.example.cdmdda.presentation.helper.LocaleHelper
import com.example.cdmdda.presentation.helper.ThemeHelper
import com.example.cdmdda.presentation.viewmodel.SettingsViewModel
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory

class SettingsActivity : BaseCompatActivity() {
    companion object {
        const val TAG = "SettingsActivity"
    }

    private val layout: ActivitySettingsBinding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }
    private val viewModel: SettingsViewModel by viewModels {
        CreateWithFactory { SettingsViewModel() }
    }
    private val settingsFragment: SettingsFragment by lazy {
        SettingsFragment(
            onThemeChange = { changeTheme(it) },
            onLocaleChange = { changeLocal(it) },
            onClearDiagnosisConfirm = { clearDiagnosis(it) },
            onClearSearchConfirm = { clearSearch(it) },
            onLogoutConfirm = { logout() }
        )
    }

    private fun logout() {
        viewModel.signOut(this)
        toast(getString(viewModel.uiWarnLogout))
        finishAffinity()
        startActivity(applicationContext.intent(MainActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarSettings)
        supportActionBar?.title = getString(viewModel.uiHeadSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(viewModel.uiSettings, settingsFragment)
                .commit()
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
        viewModel.clearDiagnosis(DiagnosisRepository(uid))
        toast(getString(viewModel.uiInfoClearDiagnosisSuccess))
        toggleLoadingUI()
    }

    private fun clearSearch(uid: String) {
        toggleLoadingUI()
        viewModel.clearSearch(SearchQueryRepository(this, uid))
        toast(getString(viewModel.uiInfoClearSearchSuccess))
        toggleLoadingUI()
    }

    private fun changeTheme(themeValue: String) {
        ThemeHelper.setTheme(this, themeValue)
        restartActivityStack()
    }

    private fun changeLocal(langValue: String) {
        LocaleHelper.setLocale(this, langValue)
        restartActivityStack()
    }

    private fun restartActivityStack() {
        finishAffinity()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        startActivity(intentWith(/*recreate*/))
    }

    private fun toggleLoadingUI() = layout.run {
        if (maskSettings.visibility == View.VISIBLE) {
            loadingClearing.visibility = View.GONE
            maskSettings.visibility = View.GONE
        } else {
            loadingClearing.visibility = View.VISIBLE
            maskSettings.visibility = View.VISIBLE
        }
    }

}

