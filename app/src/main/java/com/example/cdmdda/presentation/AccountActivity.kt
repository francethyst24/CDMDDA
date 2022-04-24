package com.example.cdmdda.presentation

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.example.cdmdda.common.AndroidUtils.LOGIN_REQUEST
import com.example.cdmdda.common.AndroidUtils.LOGIN_RESULT
import com.example.cdmdda.common.AndroidUtils.REGISTER_REQUEST
import com.example.cdmdda.common.AndroidUtils.REGISTER_RESULT
import com.example.cdmdda.common.AndroidUtils.setResultListener
import com.example.cdmdda.common.AndroidUtils.toast
import com.example.cdmdda.common.Constants.INIT_QUERIES
import com.example.cdmdda.databinding.ActivityAccountBinding
import com.example.cdmdda.presentation.adapter.AccountFragmentAdapter
import com.example.cdmdda.presentation.viewmodel.AccountViewModel
import com.google.android.material.tabs.TabLayoutMediator

class AccountActivity : BaseCompatActivity() {

    // region // declare: ViewBinding, ProgressBar, Firebase(Auth)
    private val layout by lazy { ActivityAccountBinding.inflate(layoutInflater) }
    private val model: AccountViewModel by viewModels()
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init: ViewBinding, Toolbar, ProgressBar
        setSupportActionBar(layout.toolbarAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(layout.root)

        supportFragmentManager.setResultListener(this, LOGIN_REQUEST) { bundle ->
            val greenLit = bundle.getBoolean(LOGIN_RESULT, false)
            if (greenLit) loginUser()
        }

        supportFragmentManager.setResultListener(this, REGISTER_REQUEST) { bundle ->
            val greenLit = bundle.getBoolean(REGISTER_RESULT, false)
            if (greenLit) registerUser()
        }

        // region // init: Adapter, ViewPager, Tabs
        layout.pagerAccount.apply {
            val tabTexts = listOf(model.uiTextLogin, model.uiTextRegister)
            offscreenPageLimit = tabTexts.size
            adapter = AccountFragmentAdapter(
                supportFragmentManager, lifecycle,
            )
            TabLayoutMediator(layout.tabAccount, this) { view, position ->
                tabTexts.forEachIndexed { index, tabText ->
                    if (position == index) view.text = getString(tabText)
                }
            }.attach()
        }
        // endregion
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onContextItemSelected(item)
    }

    // region // events: RegisterFragment, LoginFragment
    private fun registerUser() {
        toggleLoadingUI()
        model.registerUser()?.addOnSuccessListener(this) {
            // Move to LoginFragment
            layout.tabAccount.getTabAt(0)?.select()
            toggleLoadingUI()
            model.verifyUser()?.addOnSuccessListener {
                val header = getString(model.uiDescSentEmail)
                toast("$header ${model.email}")
            }
        }?.addOnFailureListener(this) {
            toast(model.uiWarnRegister)
            toggleLoadingUI()
        }
    }

    private fun loginUser() {
        toggleLoadingUI()
        model.loginUser()?.addOnSuccessListener(this) {
            finishAffinity()
            val restartWithUserIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(restartWithUserIntent.putExtra(INIT_QUERIES, true))
        }?.addOnFailureListener(this) {
            toast(model.uiWarnLogin)
            toggleLoadingUI()
        }
    }
    // endregion

    private fun toggleLoadingUI() {
        val current = layout.loadingAccount.visibility
        layout.loadingAccount.visibility = if (current == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

}