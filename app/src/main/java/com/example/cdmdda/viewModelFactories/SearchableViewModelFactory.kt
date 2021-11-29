package com.example.cdmdda.viewModelFactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.viewmodels.SearchableViewModel

class SearchableViewModelFactory(
    private val application: Application,
    private val query: String)
    : ViewModelProvider.AndroidViewModelFactory(application)
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchableViewModel(application, query) as T
    }
}