package com.example.cdmdda.data.repository

import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.net.Uri
import com.example.cdmdda.common.Constants.DATASET
import com.example.cdmdda.domain.model.Image
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ImageRepository constructor(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    companion object {
        const val DISEASE_ROOT = "disease_sets"
        const val DIAGNOSABLE_ROOT = "user_diagnosable"
        const val FILE_TYPE = ".jpg"
    }

    suspend fun fetchOnlineImages(
        diseaseId: String,
        onImageReceived: (List<StorageReference>) -> Unit
    ) = withContext(ioDispatcher) {
        val diseaseRef = storage.reference
            .child("$DISEASE_ROOT/$DATASET/$diseaseId")
        diseaseRef.listAll().addOnSuccessListener { result ->
            if (result.items.isEmpty()) return@addOnSuccessListener
            onImageReceived(result.items)
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

    suspend fun addDiagnosable(bmp: Bitmap, userId: String, docId: String) = withContext(ioDispatcher) {
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val data = stream.toByteArray()
        val diagnosableRef = storage.reference
            .child("$DIAGNOSABLE_ROOT/$userId/$docId$FILE_TYPE")
        return@withContext diagnosableRef.putBytes(data)
    }

    suspend fun fetchDiagnosable(
        userId: String,
        docId: String,
        onImageReceived: (Uri) -> Unit,
    ) = withContext(ioDispatcher) {
        val diagnosableRef = storage.reference
            .child("$DIAGNOSABLE_ROOT/$userId/$docId$FILE_TYPE")
        diagnosableRef.downloadUrl
            .addOnSuccessListener { onImageReceived(it) }
    }

}