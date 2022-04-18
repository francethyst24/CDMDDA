package com.example.cdmdda.data

import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.net.Uri

class SuggestionsProvider : SearchRecentSuggestionsProvider() {
    companion object {
        val AUTHORITY: String by lazy { SuggestionsProvider::class.java.name }
        const val MODE: Int = DATABASE_MODE_QUERIES
    }
    init { setupSuggestions(AUTHORITY, MODE) }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = if (UserApi.isLoggedIn) {
        super.query(uri, projection, selection, selectionArgs, sortOrder)
    } else null

}