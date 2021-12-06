package com.example.cdmdda.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.cdmdda.R
import com.example.cdmdda.model.FirestoreRepository
import com.example.cdmdda.model.ImageRepository
import com.example.cdmdda.model.dto.Disease

class DisplayDiseaseViewModel(application: Application, private val diseaseId : String) : AndroidViewModel(application), ImageRepository.ImageRepositoryListener {
    companion object { private const val TAG = "DisplayDiseaseViewModel" }
    private val context: Context get() = getApplication<Application>().applicationContext
    private val firestoreRepository = FirestoreRepository(application.getString(R.string.dataset))
    private val imageRepository = ImageRepository(context, application.getString(R.string.dataset))

    private var _disease = MutableLiveData<Disease>().apply {
        firestoreRepository.getDisease(diseaseId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen to disease document failed", e)
                return@addSnapshotListener
            }
            value = if (snapshot != null && snapshot.exists()) {
                snapshot.toObject(Disease::class.java)!!
            } else Disease("disease_name", "vector")
        }
    }
    val disease: LiveData<Disease> get() = _disease

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