package com.example.cdmdda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//bell was here
        // region -- ToolBar
        val toolbarMain = findViewById<View>(R.id.toolbar_main) as Toolbar
        setSupportActionBar(toolbarMain)
        // endregion

        auth = FirebaseAuth.getInstance()

    }

    //      FUN menu_items:OnClick
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> {
            // User chose the "Search" item, show the app search UI...
            true
        }
        R.id.action_account -> {
            startActivity(Intent(this@MainActivity, AccountActivity::class.java))
            true
        }
        R.id.action_settings ->{
            // User chose the "Settings" item, show the app settings UI...
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    //      inflate menu layout to actionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        val textUserId : TextView = findViewById<TextView>(R.id.text_user_id)
        if (user != null) {
            textUserId.text = user.email
        } else {
            textUserId.text = getString(R.string.guest)
        }
    }
}
