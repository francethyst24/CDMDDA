package com.example.cdmdda.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.example.cdmdda.common.AndroidUtils.getAssetPath
import com.example.cdmdda.common.Constants.MODEL
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import org.pytorch.torchvision.TensorImageUtils.TORCHVISION_NORM_MEAN_RGB
import org.pytorch.torchvision.TensorImageUtils.TORCHVISION_NORM_STD_RGB

class GetPytorchMLUseCase constructor(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    sealed class PytorchResult {
        data class Success(val values: FloatArray) : PytorchResult() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false
                other as Success
                if (!values.contentEquals(other.values)) return false
                return true
            }

            override fun hashCode(): Int = values.contentHashCode()
        }

        object Failure : PytorchResult()
    }

    suspend operator fun invoke(context: Context, bitmap: Bitmap) = withContext(defaultDispatcher) {
        return@withContext context.getAssetPath(MODEL)?.let { modelPath ->
            val module = try {
                Module.load(modelPath)
            } catch (e: Exception) { return@let PytorchResult.Failure }
            val inputTensorAsync = async {
                val normMeanRGB = TORCHVISION_NORM_MEAN_RGB
                val normStdRGB = TORCHVISION_NORM_STD_RGB
                TensorImageUtils.bitmapToFloat32Tensor(bitmap, normMeanRGB, normStdRGB)
            }
            val inputTensor = inputTensorAsync.await()
            val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
            return@let PytorchResult.Success(outputTensor.dataAsFloatArray)
        } ?: PytorchResult.Failure
    }

}