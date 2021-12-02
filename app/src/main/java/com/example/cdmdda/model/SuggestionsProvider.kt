package com.example.cdmdda.model

import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SuggestionsProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.example.cdmdda.model.SuggestionsProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor? = when(Firebase.auth.currentUser) {
        null -> null
        else -> super.query(uri, projection, selection, selectionArgs, sortOrder)
    }

}