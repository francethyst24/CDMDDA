package com.example.cdmdda.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.model.DataRepository
import com.example.cdmdda.model.ImageRepository
import com.example.cdmdda.model.dto.Crop


class DisplayCropViewModel(application: Application, private val cropId: String) : AndroidViewModel(application) {
    companion object { private const val TAG = "DisplayCropViewModel" }
    private val context get() = getApplication<Application>().applicationContext
    private val imageRepository = ImageRepository(context, application.getString(R.string.var_dataset))

    fun crop(context: Context) : Crop {
        val dataRepository = DataRepository(context)
        return Crop(
            dataRepository.fetchCropName(cropId),
            dataRepository.fetchCropSciName(cropId),
            dataRepository.fetchCropDescription(cropId),
            dataRepository.fetchCropDiseases(cropId),
        )
    }

    val cropBanner = liveData {
        emit(imageRepository.fetchCropBanner(cropId))
    }

    fun cropSupported(context: Context) = liveData {
        emit(DataRepository(context).fetchCropSupported(cropId))
    }

}


