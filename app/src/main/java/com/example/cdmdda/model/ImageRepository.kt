package com.example.cdmdda.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cdmdda.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ImageRepository(private val context: Context, private val DATASET: String) {
    companion object {
        const val TAG = "ImageRepository"
        const val ONE_MEGABYTE : Long = 1024 * 1024
    }
    private val storageRef = Firebase.storage.reference
    private lateinit var listener : ImageRepositoryListener

    fun fetchOfflineImages(diseaseId: String): MutableList<Bitmap> {
        val offlineImages = mutableListOf<Bitmap>()
        val resources = context.resources.obtainTypedArray(R.array.resources_image)
        val values = context.resources.getStringArray(R.array.values_image)
        for (i in values.indices) {
            if (values[i] == diseaseId) {
                val bitmap = BitmapFactory.decodeResource(
                    context.resources,
                    resources.getResourceId(i, -1)
                )
                offlineImages.add(bitmap)
            }
        }
        resources.recycle()
        return offlineImages
    }

    fun fetchOnlineImages(diseaseId: String) {
        val diseaseRef = storageRef.child("disease_sets/$DATASET/$diseaseId")
        diseaseRef.listAll().addOnCompleteListener { task ->
            if (!task.isSuccessful || task.result == null) return@addOnCompleteListener
            for (item in task.result!!.items) {
                item.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(context).asBitmap().load(uri).into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            listener.onOnlineImageReceived(resource)
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
                }
            }
        }
    }

    interface ImageRepositoryListener {
        fun onOnlineImageReceived(onlineImage: Bitmap)
    }

    fun setImageRepositoryListener(_listener : ImageRepositoryListener) {
        listener = _listener
    }

}