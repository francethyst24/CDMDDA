package com.example.cdmdda.viewModelFactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.viewmodels.DisplayDiseaseViewModel

class DisplayDiseaseViewModelFactory(private val application: Application, private val id : String) : ViewModelProvider.AndroidViewModelFactory(application)
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DisplayDiseaseViewModel(application, id) as T
    }

}