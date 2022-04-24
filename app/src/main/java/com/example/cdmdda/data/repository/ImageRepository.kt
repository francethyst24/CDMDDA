package com.example.cdmdda.data.repository

import android.content.res.Resources
import android.content.res.TypedArray
import com.example.cdmdda.common.Constants.DATASET
import com.example.cdmdda.common.ImageCallback
import com.example.cdmdda.domain.model.Image
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        const val ROOT = "disease_sets"
    }

    suspend fun fetchOnlineImages(diseaseId: String, onImageReceived: ImageCallback) = withContext(ioDispatcher) {
        val diseaseRef = Firebase.storage.reference.child("$ROOT/$DATASET/$diseaseId")
        diseaseRef.listAll().addOnSuccessListener { result ->
            if (result.items.isEmpty()) return@addOnSuccessListener
            result.items.forEach { item ->
                item.downloadUrl.addOnSuccessListener { onImageReceived(Image.Uri(it)) }
            }
        }
    }

    suspend fun fetchOfflineImages(arrId: Int, resources: Resources): List<Image> = withContext(ioDispatcher) {
        val typedArray: TypedArray
        try {
            typedArray = resources.obtainTypedArray(arrId)
        } catch (e: Resources.NotFoundException) {
            return@withContext listOf()
        }
        val imageList: MutableList<Image> = mutableListOf()
        for (i in 0 until typedArray.length()) {
            val resId = typedArray.getResourceId(i, 0)
            imageList.add(Image.Res(resId))
        }
        typedArray.recycle()
        return@withContext imageList
    }

}