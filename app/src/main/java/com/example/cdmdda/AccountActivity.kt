package com.example.cdmdda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar

class AccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        // region -- ToolBar
        val toolbarAccount = findViewById<View>(R.id.toolbar_account) as Toolbar
        setSupportActionBar(toolbarAccount)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        // endregion
    }

    override fun onContextItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@AccountActivity.finish()
            true
        }
        else -> {
            super.onContextItemSelected(item)
        }
    }

}