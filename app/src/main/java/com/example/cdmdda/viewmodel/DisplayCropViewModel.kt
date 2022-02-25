package com.example.cdmdda.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.model.ImageRepository
import com.example.cdmdda.model.TextRepository
import com.example.cdmdda.model.dto.Crop
import kotlinx.coroutines.Dispatchers


class DisplayCropViewModel(application: Application, private val cropId: String, textRepository: TextRepository) : AndroidViewModel(application) {
    companion object { private const val TAG = "DisplayCropViewModel" }
    private val context get() = getApplication<Application>().applicationContext
    private val imageRepository = ImageRepository(context, application.getString(R.string.dataset))

    private var _crop = Crop(
        textRepository.fetchCropName(cropId),
        textRepository.fetchCropSciName(cropId),
        textRepository.fetchCropDescription(cropId),
        textRepository.fetchCropDiseases(cropId),
    )
    val crop = _crop

    val cropBanner = liveData(Dispatchers.Default) {
        emit(imageRepository.fetchCropBanner(cropId))
    }

    val cropIcon = liveData(Dispatchers.Default) {
        emit(imageRepository.fetchCropIcon(cropId))
    }

}


