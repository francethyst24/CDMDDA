package com.example.cdmdda.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.cdmdda.view.utils.ResourceUtils
import com.example.cdmdda.view.utils.toResourceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ImageRepository(private val context: Context, private val DATASET: String) {
    private val storageRef = Firebase.storage.reference
    private lateinit var listener : ImageRepositoryListener

    fun fetchCropBanner(cropId: String) : Int {
        val uri = "banner_${cropId.toResourceId()}"
        return ResourceUtils.getDrawableById(context, uri)
    }

    fun fetchOfflineImages(diseaseId: String, offlineImages: MutableList<Bitmap> = mutableListOf()) : MutableList<Bitmap> {
        try {
            context.resources.obtainTypedArray(
                ResourceUtils.getDrawableArrayById(context, "drawables_${diseaseId.toResourceId()}")
            ).apply {
                for (i in 0 until length()) {
                    BitmapFactory.decodeResource(context.resources, getResourceId(i, -1))?.let {
                        offlineImages.add(it)
                    }
                }
                recycle()
            }
        } catch (e: Exception) { Log.w("ImageRepository", "NotFoundException handled") }
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