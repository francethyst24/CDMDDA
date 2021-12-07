package com.example.cdmdda.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.model.FirestoreRepository
import com.example.cdmdda.model.ImageRepository
import com.example.cdmdda.model.dto.Crop
import kotlinx.coroutines.Dispatchers


class DisplayCropViewModel(application: Application, private val cropId: String) : AndroidViewModel(application) {
    companion object { private const val TAG = "DisplayCropViewModel" }
    private val context: Context get() = getApplication<Application>().applicationContext
    private val repository = FirestoreRepository(application.getString(R.string.dataset))
    private val imageRepository = ImageRepository(context, application.getString(R.string.dataset))

    private var _crop = MutableLiveData<Crop>().apply {
        repository.getCrop(cropId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen to crop document failed", e)
                return@addSnapshotListener
            }
            value = if (snapshot != null && snapshot.exists()) {
                snapshot.toObject(Crop::class.java)!!
            } else Crop("crop_name")
        }
    }
    val crop : LiveData<Crop> get() = _crop

    val cropBanner = liveData(Dispatchers.Default) {
        emit(imageRepository.fetchCropBanner(cropId))
    }

    val cropIcon = liveData(Dispatchers.Default) {
        emit(imageRepository.fetchCropIcon(cropId))
    }

}


