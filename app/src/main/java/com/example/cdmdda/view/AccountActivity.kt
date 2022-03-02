package com.example.cdmdda.view

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivityAccountBinding
import com.example.cdmdda.view.adapter.AccountFragmentAdapter
import com.example.cdmdda.view.fragment.LoginFragment
import com.example.cdmdda.view.fragment.RegisterFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountActivity : BaseCompatActivity(), RegisterFragment.RegisterFragmentListener, LoginFragment.LoginFragmentListener {

    companion object { private const val TAG = "AccountActivity" }

    // region // declare: ViewBinding, ProgressBar, Firebase(Auth)
    private lateinit var layout: ActivityAccountBinding
    private lateinit var auth : FirebaseAuth

    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init: ViewBinding, Toolbar, ProgressBar
        layout = ActivityAccountBinding.inflate(layoutInflater).apply {
            setSupportActionBar(toolbarAccount)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setContentView(root)
        }

        // region // init: Adapter, ViewPager, Tabs
        val pagerAdapter = AccountFragmentAdapter(supportFragmentManager, lifecycle)
        layout.pagerAccount.apply {
            offscreenPageLimit = 2
            adapter = pagerAdapter
            TabLayoutMediator(layout.tabAccount, this) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.ui_text_login)
                    1 -> tab.text = getString(R.string.ui_text_register)
                }
            }.attach()
        }
        // endregion

        // init: Firebase(Auth)
        auth = Firebase.auth
    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { this@AccountActivity.finish(); true }
        else -> { super.onContextItemSelected(item) }
    }

    // region // events: RegisterFragment, LoginFragment
    override fun onRegisterClick(email: String, password: String) {
        layout.loadingAccount.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this@AccountActivity) { createUserTask ->
                if (createUserTask.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { sendEmailTask ->
                        if (sendEmailTask.isSuccessful) {
                            Toast.makeText(this@AccountActivity,
                                "Verification email sent to".plus(" $email"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this@AccountActivity,
                                "Failed to send verification email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    // this@AccountActivity.finish()
                    layout.tabAccount.getTabAt(0)?.select()
                    layout.loadingAccount.visibility = View.INVISIBLE
                }
                else {
                    Log.w(TAG, "createUserWithEmail:failure", createUserTask.exception)
                    Toast.makeText(this@AccountActivity,
                        R.string.fui_email_account_creation_error,
                        Toast.LENGTH_SHORT
                    ).show()
                    layout.loadingAccount.visibility = View.INVISIBLE
                }
            }
    }

    override fun onLoginClick(email: String, password: String) {
        layout.loadingAccount.visibility = View.VISIBLE
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
                    layout.loadingAccount.visibility = View.INVISIBLE
                }
            }
    }
    // endregion

}