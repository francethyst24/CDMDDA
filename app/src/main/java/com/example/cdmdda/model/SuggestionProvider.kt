package com.example.cdmdda.model

import android.content.SearchRecentSuggestionsProvider

class SuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.example.cdmdda.model.SuggestionProvider"
        const val MODE: Int = DATABASE_MODE_QUERIES
    }



}