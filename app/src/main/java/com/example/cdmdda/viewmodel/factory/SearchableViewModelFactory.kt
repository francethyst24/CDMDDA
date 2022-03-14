package com.example.cdmdda.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.model.DataRepository
import com.example.cdmdda.viewmodel.SearchableViewModel

class SearchableViewModelFactory(
    private val application: Application,
    private val query: String,
)
    : ViewModelProvider.AndroidViewModelFactory(application)
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchableViewModel(application, query) as T
    }
}