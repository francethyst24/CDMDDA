package com.example.cdmdda.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.cdmdda.R
import com.example.cdmdda.model.FirestoreRepository
import com.example.cdmdda.model.dto.Crop
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DisplayCropViewModel(application: Application, cropId: String) : AndroidViewModel(application) {
    companion object { private val TAG = "DisplayCropViewModel" }
    private val repository = FirestoreRepository(application.getString(R.string.dataset))

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

}


