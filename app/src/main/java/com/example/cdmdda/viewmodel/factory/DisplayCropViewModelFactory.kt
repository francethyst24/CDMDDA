package com.example.cdmdda.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.cdmdda.model.DataRepository
import com.example.cdmdda.viewmodel.DisplayCropViewModel

class DisplayCropViewModelFactory(private val application: Application, private val id : String) : AndroidViewModelFactory(application)
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DisplayCropViewModel(application, id) as T
    }

}