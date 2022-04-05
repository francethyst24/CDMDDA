package com.example.cdmdda.presentation

import android.content.Intent
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.cdmdda.R
import com.example.cdmdda.data.SuggestionsProvider
import com.example.cdmdda.databinding.ActivityAccountBinding
import com.example.cdmdda.presentation.adapter.AccountFragmentAdapter
import com.example.cdmdda.presentation.viewmodel.AccountViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountActivity : BaseCompatActivity() {

    companion object { private const val TAG = "AccountActivity" }

    // region // declare: ViewBinding, ProgressBar, Firebase(Auth)
    private lateinit var layout: ActivityAccountBinding
    private val model: AccountViewModel by viewModels()
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
        val pagerAdapter = AccountFragmentAdapter(
            supportFragmentManager,
            lifecycle,
            onRegisterClick = { registerUser() },
            onLoginClick = { loginUser() }
        )
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
    private fun registerUser() {
        layout.loadingAccount.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(model.email!!, model.password!!)
            .addOnCompleteListener(this@AccountActivity) { createUserTask ->
                if (createUserTask.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { sendEmailTask ->
                        if (sendEmailTask.isSuccessful) {
                            Toast.makeText(this@AccountActivity,
                                "Verification email sent to".plus(" ${model.email}"),
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

    private fun loginUser() {
        layout.loadingAccount.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(model.email!!, model.password!!)
            .addOnCompleteListener(this@AccountActivity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    finishAffinity()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                }
                else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@AccountActivity,
                        R.string.fui_error_unknown,
                        Toast.LENGTH_SHORT)
                        .show()
                    layout.loadingAccount.visibility = View.INVISIBLE
                }
            }
    }
    // endregion

}