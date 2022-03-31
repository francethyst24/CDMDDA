package com.example.cdmdda.domain.usecase

import android.content.Context
import com.example.cdmdda.data.dto.UserInput
import com.example.cdmdda.domain.usecase.InferTfliteModelUseCase.InferResult
import com.example.cdmdda.presentation.helper.ResourceHelper
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class DiagnoseDiseaseUseCase(
    private val preprocessBitmapUseCase: PreprocessBitmapUseCase,
    private val inferTfliteModelUseCase: InferTfliteModelUseCase,
    private var job: Job = Job(),
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(context: Context, input: UserInput): String = withContext(job + defaultDispatcher) {
        val labels = withContext(ioDispatcher) { ResourceHelper(context).tfliteLabels }
        val defaultIndex = labels.indexOfFirst { it == "null" }
        preprocessBitmapUseCase(input, context.contentResolver)?.let { bitmap ->
            val result = inferTfliteModelUseCase(context, bitmap)
            if (result is InferResult.Success) {
                val index = result.values.indexOfFirst { it == 1.0f }
                delay(TimeUnit.SECONDS.toMillis(5))
                return@withContext labels[if (index != -1) index else defaultIndex]
            }
        }
        return@withContext labels[defaultIndex]
    }

    fun cancelDiagnosis() {
        job.cancel()
        job = Job()
    }

}