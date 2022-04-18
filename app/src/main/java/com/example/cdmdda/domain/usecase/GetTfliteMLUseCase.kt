package com.example.cdmdda.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.example.cdmdda.ml.DiseaseDetection
import com.example.cdmdda.data.repository.DataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class GetTfliteMLUseCase(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dataType: DataType = DataType.FLOAT32,
    private val dim: Int = DataRepository.DIM,
) {
    sealed class InferResult {
        data class Success(val values: FloatArray) : InferResult() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false
                other as Success
                if (!values.contentEquals(other.values)) return false
                return true
            }

            override fun hashCode(): Int = values.contentHashCode()
        }

        object Failure : InferResult()
    }

    suspend operator fun invoke(context: Context, bitmap: Bitmap) = withContext(ioDispatcher) {
        runCatching { DiseaseDetection.newInstance(context) }.fold(
            onSuccess = { InferResult.Success(it.infer(bitmap)) },
            onFailure = { InferResult.Failure }
        )
    }

    private suspend fun DiseaseDetection.infer(bitmap: Bitmap) = withContext(defaultDispatcher) {
        val tensorImage = bitmap.toTensorImage()

        // Creates input for reference.
        val inputFeature0 = getTensorBuffer()
        inputFeature0.loadBuffer(tensorImage.buffer)

        // Runs model inference and gets result.
        val outputs = process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        return@withContext outputFeature0.floatArray
    }

    private suspend fun Bitmap.toTensorImage(): TensorImage = withContext(defaultDispatcher) {
        TensorImage.createFrom(TensorImage.fromBitmap(this@toTensorImage), dataType)
    }

    private suspend fun getTensorBuffer(): TensorBuffer = withContext(defaultDispatcher) {
        TensorBuffer.createFixedSize(intArrayOf(1, dim, dim, 3), dataType)
    }
}