package com.example.cdmdda.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cdmdda.R
import com.example.cdmdda.model.ImageRepository
import com.example.cdmdda.model.DataRepository
import com.example.cdmdda.model.dto.Disease

class DisplayDiseaseViewModel(application: Application, private val diseaseId : String, dataRepository: DataRepository) : AndroidViewModel(application), ImageRepository.ImageRepositoryListener {
    companion object { private const val TAG = "DisplayDiseaseViewModel" }
    private val context: Context get() = getApplication<Application>().applicationContext
    private val imageRepository = ImageRepository(context, application.getString(R.string.var_dataset))

    private var _disease = Disease(
        dataRepository.fetchDiseaseName(diseaseId),
        dataRepository.fetchDiseaseVector(diseaseId),
        dataRepository.fetchDiseaseCause(diseaseId),
        dataRepository.fetchDiseaseTreatment(diseaseId),
        dataRepository.fetchDiseaseSymptoms(diseaseId),
        dataRepository.fetchDiseaseCropIds(diseaseId),
        dataRepository.fetchDiseaseCropNames(diseaseId),
    )
    val disease = _disease

    private val imageBitmaps = imageRepository.fetchOfflineImages(diseaseId)
    private val _diseaseImages = MutableLiveData<List<Bitmap>>(imageBitmaps)
    val diseaseImages: LiveData<List<Bitmap>> get() = _diseaseImages

    fun fetchDiseaseImages() {
        imageRepository.setImageRepositoryListener(this@DisplayDiseaseViewModel)
        imageRepository.fetchOnlineImages(diseaseId)
    }

    override fun onOnlineImageReceived(onlineImage: Bitmap) {
        imageBitmaps.add(onlineImage)
        _diseaseImages.postValue(imageBitmaps)
    }

}