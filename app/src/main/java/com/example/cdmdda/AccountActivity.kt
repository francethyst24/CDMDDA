package com.example.cdmdda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.cdmdda.adapters.AccountFragmentAdapter
import com.example.cdmdda.fragments.RegisterFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class AccountActivity : AppCompatActivity()
    , RegisterFragment.RegisterFragmentListener {

    private lateinit var auth : FirebaseAuth
    private lateinit var pbProgress : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        // region -- UI elements
        val toolbarAccount = findViewById<View>(R.id.toolbar_account) as Toolbar
        setSupportActionBar(toolbarAccount)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        pbProgress = findViewById(R.id.pb_account_progress)
        // endregion

        // region -- Tabs Implementation
        val tabAccount : TabLayout = findViewById(R.id.tab_account)
        val pagerAccount : ViewPager2 = findViewById(R.id.pager_account)
        val pagerAdapter = AccountFragmentAdapter(supportFragmentManager, lifecycle)
        pagerAccount.offscreenPageLimit = 2
        pagerAccount.adapter = pagerAdapter

        TabLayoutMediator(tabAccount, pagerAccount) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.text_login)
                1 -> tab.text = getString(R.string.text_register)
            }
        }.attach()
        // endregion

        auth = FirebaseAuth.getInstance();
    }

    //      FUN back_button:OnClick
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@AccountActivity.finish()
            true
        }
        else -> {
            super.onContextItemSelected(item)
        }
    }

    override fun onRegisterClick(email: String, password: String) {
        pbProgress.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{

                this@AccountActivity.finish()
            }
            .addOnFailureListener{ e ->
                Toast.makeText(this@AccountActivity,
                    "Login failed due to ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        pbProgress.visibility = View.GONE
    }

}