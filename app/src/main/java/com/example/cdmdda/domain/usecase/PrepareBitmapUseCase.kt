package com.example.cdmdda.domain.usecase

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import com.example.cdmdda.common.Constants.DIM
import com.example.cdmdda.domain.model.Diagnosable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrepareBitmapUseCase constructor(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(input: Diagnosable, resolver: ContentResolver) = withContext(ioDispatcher) {
        convert(input, resolver)?.rescale(DIM, true)
    }

    @Suppress("DEPRECATION")
    private suspend fun convert(input: Diagnosable, resolver: ContentResolver): Bitmap? = withContext(ioDispatcher) {
        return@withContext when (input) {
            is Diagnosable.Bmp -> input.value
            is Diagnosable.Uri -> {
                if (Build.VERSION.SDK_INT < 28) {
                    runCatching {
                        MediaStore.Images.Media.getBitmap(resolver, input.value)
                    }.fold(onSuccess = { it }, onFailure = { null })
                } else {
                    runCatching {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, input.value))
                    }.fold(
                        onSuccess = { it.copy(Bitmap.Config.ARGB_8888, true) },
                        onFailure = { null }
                    )
                }
            }
        }
    }

    private suspend fun Bitmap.rescale(dim: Int, filter: Boolean) = withContext(defaultDispatcher) {
        Bitmap.createScaledBitmap(this@rescale, dim, dim, filter)
    }

}