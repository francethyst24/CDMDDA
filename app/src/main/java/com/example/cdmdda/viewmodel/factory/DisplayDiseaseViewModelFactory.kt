package com.example.cdmdda.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.model.DataRepository
import com.example.cdmdda.viewmodel.DisplayDiseaseViewModel

class DisplayDiseaseViewModelFactory(
    private val application: Application,
    private val id: String
)
    : ViewModelProvider.AndroidViewModelFactory(application)
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DisplayDiseaseViewModel(application, id) as T
    }

}