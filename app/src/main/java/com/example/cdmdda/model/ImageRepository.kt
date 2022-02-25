package com.example.cdmdda.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cdmdda.R
import com.example.cdmdda.view.utils.ResourceUtils
import com.example.cdmdda.view.utils.StringUtils
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ImageRepository(private val context: Context, private val DATASET: String) {
    private val storageRef = Firebase.storage.reference
    private lateinit var listener : ImageRepositoryListener

    fun fetchCropBanner(cropId: String) : Drawable {
        val uri = "banner_${StringUtils.toResourceId(cropId)}"
        return ResourceUtils.getDrawableById(context, uri)
    }

    fun fetchOfflineImages(diseaseId: String, offlineImages: MutableList<Bitmap> = mutableListOf()) : MutableList<Bitmap> {
        val resIds = context.resources.obtainTypedArray(R.array.drawable_images)
        val values = context.resources.getStringArray(R.array.string_images)
        for (i in values.indices) {
            if (values[i] == diseaseId) {
                val bitmap = BitmapFactory.decodeResource(context.resources, resIds.getResourceId(i, -1))
                offlineImages.add(bitmap)
            }
        }
        resIds.recycle()
        return offlineImages
    }

    fun fetchOnlineImages(diseaseId: String) {
        val diseaseRef = storageRef.child("disease_sets/$DATASET/$diseaseId")
        diseaseRef.listAll().addOnSuccessListener {
            if (it == null) return@addOnSuccessListener
            for (item in it.items) {
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