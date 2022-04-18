package com.example.cdmdda.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.example.cdmdda.common.Constants.NULL
import com.example.cdmdda.common.Constants.OUT_OF_BOUNDS
import com.example.cdmdda.common.Constants.TFLITE_INFERRED_VALUE
import com.example.cdmdda.common.StringArray
import com.example.cdmdda.data.dto.UserInput
import com.example.cdmdda.domain.usecase.GetPytorchMLUseCase.PytorchResult
import com.example.cdmdda.domain.usecase.GetTfliteMLUseCase.InferResult
import com.example.cdmdda.presentation.helper.ResourceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class GetDiseaseDiagnosisUseCase(
    private val prepareBitmapUseCase: PrepareBitmapUseCase,
    private val getTfliteMLUseCase: GetTfliteMLUseCase,
    private val getPytorchMLUseCase: GetPytorchMLUseCase,
    private val resourceHelper: ResourceHelper,
    private var job: Job = Job(),
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    //private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    // TODO: cleanup comments after tflite -> pytorch migration
    //private val labels: StringArray by lazy { resourceHelper.tfliteLabels }
    private val labels: StringArray by lazy { resourceHelper.pytorchLabels }
    private val defaultIndex :  Int by lazy { labels.indexOfFirst { it == NULL } }

    suspend operator fun invoke(context: Context, input: UserInput) = withContext(job + defaultDispatcher) {
        prepareBitmapUseCase(input, context.contentResolver)?.let { bitmap ->
            //tflite(context, bitmap)?.let { return@withContext it }
            pytorch(context, bitmap)?.let { return@withContext it }
        } ?: return@withContext labels[defaultIndex]
    }

    fun cancelDiagnosis() {
        job.cancel()
        job = Job()
    }

    private suspend fun pytorch(context: Context, bitmap: Bitmap): String? {
        val res = getPytorchMLUseCase(context, bitmap)
        if (res is PytorchResult.Success) { with(res.values) {
            val index = indexOfFirst { it == (maxOrNull() ?: -Float.MAX_VALUE) }
            return labels[if (index != OUT_OF_BOUNDS) index else defaultIndex]
        } }
        return null
    }

    private suspend fun tflite(context: Context, bitmap: Bitmap): String? {
        val result = getTfliteMLUseCase(context, bitmap)
        if (result is InferResult.Success) {
            val index = result.values.indexOfFirst { it == TFLITE_INFERRED_VALUE }
            return labels[if (index != OUT_OF_BOUNDS) index else defaultIndex]
        }
        return null
    }

}