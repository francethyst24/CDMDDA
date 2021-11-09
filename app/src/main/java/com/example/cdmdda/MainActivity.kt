package com.example.cdmdda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.fragments.LogoutFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), LogoutFragment.LogoutFragmentListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // region -- ViewBinding init
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_CDMDDA)
        setContentView(binding.root)
        // endregion

        //bell was here

        // region -- ToolBar init
        val toolbarMain = binding.toolbarMain
        setSupportActionBar(toolbarMain)
        // endregion

        // region -- Firebase init
        auth = Firebase.auth
        // endregion

    }

    // HANDLE menu events
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> {
            true
        }
        R.id.action_account -> {
            if (auth.currentUser == null) {
                startActivity(Intent(this@MainActivity, AccountActivity::class.java))
            } else {
                val dialog = LogoutFragment()
                dialog.show(supportFragmentManager, "LogoutFragment")
            }
            true
        }
        R.id.action_settings -> {
            true
        }
        else -> { super.onOptionsItemSelected(item) }
    }

    // INFLATE menu to actionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // UPDATE on auth state change
    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        val textUserId : TextView = binding.textUserId
        if (user != null) textUserId.text = user.email
        else textUserId.text = getString(R.string.text_guest)
    }

    // region -- HANDLE logoutDialogFragment events
    override fun onLogoutClick(fragment: AppCompatDialogFragment) {
        auth.signOut()
        finish(); startActivity(this@MainActivity.intent)
        Toast.makeText(this@MainActivity, "Logged out", Toast.LENGTH_SHORT).show()
    }

    override fun onCancelClick(fragment: AppCompatDialogFragment) {}
    // endregion

}
