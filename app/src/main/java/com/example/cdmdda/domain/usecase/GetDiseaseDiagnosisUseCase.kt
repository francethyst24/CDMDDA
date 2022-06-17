package com.example.cdmdda.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import com.example.cdmdda.common.Constants.NULL
import com.example.cdmdda.common.Constants.OUT_OF_BOUNDS
import com.example.cdmdda.common.StringArray
import com.example.cdmdda.domain.model.Diagnosable
import com.example.cdmdda.domain.usecase.GetPytorchMLUseCase.PytorchResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

class GetDiseaseDiagnosisUseCase(
    private val prepareBitmapUseCase: PrepareBitmapUseCase,
    private val getPytorchMLUseCase: GetPytorchMLUseCase,
    private val labels: StringArray,
    private var job: Job = Job(),
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private fun StringArray.defaultValue(): Pair<String, Float> = get(indexOfFirst { it == NULL }) to 1.0f

    suspend operator fun invoke(context: Context, input: Diagnosable): Pair<String, Float> {
        return withContext(job + defaultDispatcher) {
            prepareBitmapUseCase(input, context.contentResolver)?.let { bitmap ->
                pytorch(context, bitmap) ?: labels.defaultValue()
            } ?: labels.defaultValue()
        }
    }

    fun cancelDiagnosis() {
        job.cancel()
        job = Job()
    }

    private suspend fun pytorch(context: Context, bitmap: Bitmap): Pair<String, Float>? {
        val res = getPytorchMLUseCase(context, bitmap)
        if (res is PytorchResult.Success) {
            res.values.let { floatArr ->
                val index = floatArr.indexOfFirst { it == (floatArr.maxOrNull() ?: -Float.MAX_VALUE) }
                return if (index != OUT_OF_BOUNDS) {
                    labels[index] to floatArr[index]
                } else labels.defaultValue()
            }
        }
        return null
    }

}