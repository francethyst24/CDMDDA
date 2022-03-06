package com.example.cdmdda.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.model.ImageRepository
import com.example.cdmdda.model.DataRepository
import com.example.cdmdda.model.dto.Crop
import kotlinx.coroutines.Dispatchers


class DisplayCropViewModel(application: Application, private val cropId: String, dataRepository: DataRepository) : AndroidViewModel(application) {
    companion object { private const val TAG = "DisplayCropViewModel" }
    private val context get() = getApplication<Application>().applicationContext
    private val imageRepository = ImageRepository(context, application.getString(R.string.var_dataset))

    private var _crop = Crop(
        dataRepository.fetchCropName(cropId),
        dataRepository.fetchCropSciName(cropId),
        dataRepository.fetchCropDescription(cropId),
        dataRepository.fetchCropDiseases(cropId),
    )
    val crop = _crop

    val cropBanner = liveData(Dispatchers.Default) {
        emit(imageRepository.fetchCropBanner(cropId))
    }
    /*
    val cropIcon = liveData(Dispatchers.Default) {
        emit(imageRepository.fetchCropIcon(cropId))
    }
     */

}


