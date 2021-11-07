package com.example.cdmdda

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.cdmdda.adapters.AccountFragmentAdapter
import com.example.cdmdda.fragments.LoginFragment
import com.example.cdmdda.fragments.RegisterFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class AccountActivity : AppCompatActivity(),
    RegisterFragment.RegisterFragmentListener,
    LoginFragment.LoginFragmentListener
{

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

        // region -- TAB of fragments
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

    // HANDLE menu onClick
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@AccountActivity.finish()
            true
        }
        else -> {
            super.onContextItemSelected(item)
        }
    }

    // region -- HANDLE fragment events
    override fun onRegisterClick(email: String, password: String) {
        pbProgress.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@AccountActivity) { task ->
                when {
                    task.isSuccessful -> {
                        Log.d(TAG, "createUserWithEmail:success")
                        this@AccountActivity.finish()
                    }
                    else -> {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@AccountActivity,
                            R.string.fui_email_account_creation_error,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        pbProgress.visibility = View.GONE
    }

    override fun onLoginClick(email: String, password: String) {
        pbProgress.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@AccountActivity) { task ->
                when {
                    task.isSuccessful -> {
                        Log.d(TAG, "signInWithEmail:success")
                        this@AccountActivity.finish()
                    }
                    else -> {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@AccountActivity,
                            R.string.fui_trouble_signing_in,
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        pbProgress.visibility = View.GONE
    }
    // endregion
}