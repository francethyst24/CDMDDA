package com.example.cdmdda.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.cdmdda.model.TextRepository
import com.example.cdmdda.viewmodel.DisplayCropViewModel

class DisplayCropViewModelFactory(private val application: Application, private val id : String, private val textRepository: TextRepository) : AndroidViewModelFactory(application)
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DisplayCropViewModel(application, id, textRepository) as T
    }

}