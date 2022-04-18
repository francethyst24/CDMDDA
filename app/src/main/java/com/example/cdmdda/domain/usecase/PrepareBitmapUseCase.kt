package com.example.cdmdda.domain.usecase

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import com.example.cdmdda.data.dto.UserInput
import com.example.cdmdda.presentation.helper.ResourceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PrepareBitmapUseCase(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(input: UserInput, resolver: ContentResolver) = withContext(ioDispatcher) {
        convert(input, resolver)?.rescale(ResourceHelper.DIM, true)
    }

    @Suppress("DEPRECATION")
    private suspend fun convert(input: UserInput, resolver: ContentResolver): Bitmap? = withContext(ioDispatcher) {
        return@withContext when (input) {
            is UserInput.Bmp -> input.value
            is UserInput.Uri -> {
                if (Build.VERSION.SDK_INT < 28) {
                    runCatching {
                        MediaStore.Images.Media.getBitmap(resolver, input.value)
                    }.fold(onSuccess = { it }, onFailure =  { null })
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