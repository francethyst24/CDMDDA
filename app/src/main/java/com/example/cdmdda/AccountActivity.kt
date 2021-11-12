package com.example.cdmdda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.cdmdda.adapters.AccountFragmentAdapter
import com.example.cdmdda.databinding.ActivityAccountBinding
import com.example.cdmdda.fragments.LoginFragment
import com.example.cdmdda.fragments.RegisterFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountActivity : AppCompatActivity(),
    RegisterFragment.RegisterFragmentListener,
    LoginFragment.LoginFragmentListener
{
    private val TAG = "AccountActivity"

    // region -- declare: ViewBinding, FirebaseAuth, ProgressBar
    private lateinit var binding: ActivityAccountBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var pbProgress : ProgressBar
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // region -- init: ViewBinding, Toolbar
        binding = ActivityAccountBinding.inflate(layoutInflater)
        val toolbarAccount = binding.toolbarAccount
        setSupportActionBar(toolbarAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
        // endregion

        pbProgress = binding.pbAccountProgress

        // region -- init: Tabs, Pager, Fragment
        val tabAccount : TabLayout = binding.tabAccount
        val pagerAccount : ViewPager2 = binding.pagerAccount
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

        // region -- init: Firebase - Auth
        auth = Firebase.auth;
        // endregion
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@AccountActivity.finish()
            true
        }
        else -> super.onContextItemSelected(item)
    }

    // region -- events: FragmentOnClick
    override fun onRegisterClick(email: String, password: String) {
        pbProgress.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@AccountActivity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    this@AccountActivity.finish()
                }
                else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this@AccountActivity,
                        R.string.fui_email_account_creation_error,
                        Toast.LENGTH_SHORT)
                        .show()
                    pbProgress.visibility = View.INVISIBLE
                }
            }
    }

    override fun onLoginClick(email: String, password: String) {
        pbProgress.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@AccountActivity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    this@AccountActivity.finish()
                }
                else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@AccountActivity,
                        R.string.fui_trouble_signing_in,
                        Toast.LENGTH_SHORT)
                        .show()
                    pbProgress.visibility = View.INVISIBLE
                }
            }
    }
    // endregion

}