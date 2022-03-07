package com.example.cdmdda.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.model.DataRepository
import com.example.cdmdda.model.ImageRepository
import com.example.cdmdda.model.dto.Disease

class DisplayDiseaseViewModel(application: Application, private val diseaseId : String) : AndroidViewModel(application), ImageRepository.ImageRepositoryListener {
    companion object { private const val TAG = "DisplayDiseaseViewModel" }
    private val context: Context get() = getApplication<Application>().applicationContext
    private val imageRepository = ImageRepository(context, application.getString(R.string.var_dataset))

    fun disease(context: Context) : Disease {
        val dataRepository = DataRepository(context)
        return Disease(
            dataRepository.fetchDiseaseName(diseaseId),
            dataRepository.fetchDiseaseVector(diseaseId),
            dataRepository.fetchDiseaseCause(diseaseId),
            dataRepository.fetchDiseaseTreatment(diseaseId),
            dataRepository.fetchDiseaseSymptoms(diseaseId),
            dataRepository.fetchDiseaseCropIds(diseaseId),
            dataRepository.fetchDiseaseCropNames(diseaseId),
        )
    }

    fun fetchDiseaseImages() {
        imageRepository.setImageRepositoryListener(this@DisplayDiseaseViewModel)
        imageRepository.fetchOnlineImages(diseaseId)
    }

    val imageBitmaps = imageRepository.fetchOfflineImages(diseaseId)
    private val _diseaseImages = MutableLiveData(imageBitmaps.size - 1)
    val diseaseImages: LiveData<Int> get() = _diseaseImages

    override fun onOnlineImageReceived(onlineImage: Bitmap) {
        imageBitmaps.add(onlineImage)
        _diseaseImages.postValue(imageBitmaps.size - 1)
    }

    fun diseaseSupported(context: Context) = liveData {
        emit(DataRepository(context).fetchDiseaseSupported(diseaseId))
    }

}