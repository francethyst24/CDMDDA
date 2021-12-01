package com.example.cdmdda.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.cdmdda.R
import com.example.cdmdda.model.dto.Crop
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DisplayCropViewModel(application: Application, cropId: String) : AndroidViewModel(application) {
    private val TAG = "DisplayCropViewModel"

    private var db: FirebaseFirestore = Firebase.firestore
    private val cropRef = db.collection("crop_sets")
        .document(application.getString(R.string.dataset))
        .collection("crops").document(cropId)

    var crop = MutableLiveData<Crop>().apply {
        cropRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen to crop document failed", e)
                return@addSnapshotListener
            }
            value = if (snapshot != null && snapshot.exists()) {
                snapshot.toObject(Crop::class.java)!!
            } else Crop("crop_name", "sci_name")
        }
    }

}


