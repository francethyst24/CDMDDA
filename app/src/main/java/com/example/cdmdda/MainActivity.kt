package com.example.cdmdda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // region -- ToolBar
        val toolbarMain = findViewById<View>(R.id.toolbarMain) as Toolbar
        setSupportActionBar(toolbarMain)
        // endregion

    }

    //      menu_items:OnClick event
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> {
            // User chose the "Search" item, show the app search UI...
            true
        }
        R.id.action_account -> {

            true
        }
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


}
