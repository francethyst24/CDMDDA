package com.example.cdmdda.viewModelFactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.cdmdda.viewmodels.DisplayCropViewModel

class IdViewModelFactory(private val application: Application, private val id : String) : AndroidViewModelFactory(application)
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DisplayCropViewModel(application, id) as T
    }

}