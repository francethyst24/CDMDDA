package com.example.cdmdda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.cdmdda.adapters.AccountFragmentAdapter
import com.example.cdmdda.fragments.LoginFragment
import com.example.cdmdda.fragments.RegisterFragment
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        // region -- ToolBar
        val toolbarAccount = findViewById<View>(R.id.toolbar_account) as Toolbar
        setSupportActionBar(toolbarAccount)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // endregion

        // region -- Tabs Implementation
        val tabAccount : TabLayout = findViewById(R.id.tab_account)

        val pagerAccount : ViewPager2 = findViewById(R.id.pager_account)

        val pagerAdapter = AccountFragmentAdapter(supportFragmentManager, lifecycle)
        pagerAccount.adapter = pagerAdapter

        TabLayoutMediator(tabAccount, pagerAccount) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.button_login)
                }
                1 -> {
                    tab.text = getString(R.string.button_register)
                }
            }
        }.attach()
        // endregion
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@AccountActivity.finish()
            true
        }
        else -> {
            super.onContextItemSelected(item)
        }
    }

}