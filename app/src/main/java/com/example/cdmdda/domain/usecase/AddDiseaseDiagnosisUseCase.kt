package com.example.cdmdda.domain.usecase

import android.content.Context
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.data.repository.ImageRepository
import com.example.cdmdda.domain.model.Diagnosable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddDiseaseDiagnosisUseCase(
    private val imageRepository: ImageRepository = ImageRepository(),
    private val prepareBitmapUseCase: PrepareBitmapUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private lateinit var diagnosisRepository: DiagnosisRepository

    suspend operator fun invoke(userId: String) = withContext(ioDispatcher) {
        diagnosisRepository = DiagnosisRepository(userId)
    }

    suspend fun add(id: String, confidenceLvl: Float) = withContext(ioDispatcher) {
        diagnosisRepository.add(id, confidenceLvl)
    }

    suspend fun addDiagnosable(
        context: Context,
        diagnosable: Diagnosable,
        userId: String,
        docId: String,
    ) = withContext(ioDispatcher) {
        prepareBitmapUseCase(diagnosable, context.contentResolver)?.let { bitmap ->
            imageRepository.addDiagnosable(bitmap, userId, docId)
        }
    }
}