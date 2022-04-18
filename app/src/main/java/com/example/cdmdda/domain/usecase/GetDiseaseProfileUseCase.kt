package com.example.cdmdda.domain.usecase

import com.example.cdmdda.data.dto.*
import com.example.cdmdda.data.repository.DiseaseDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetDiseaseProfileUseCase(
    private val disease: DiseaseDataRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(dto: Disease) = withContext(ioDispatcher) {
        return@withContext when (dto) {
            is DiseaseItem -> {
                DiseaseProfile(
                    dto.id,
                    disease.vector(dto.id),
                    disease.cause(dto.id),
                    disease.symptoms(dto.id),
                    disease.treatments(dto.id),
                    disease.cropsAffected(dto.id),
                    dto.isDetectable,
                    disease.offlineImages(dto.id),
                )
            }
            is DiseaseDiagnosis, is DiseaseText -> {
                DiseaseProfile(
                    dto.id,
                    disease.vector(dto.id),
                    disease.cause(dto.id),
                    disease.symptoms(dto.id),
                    disease.treatments(dto.id),
                    disease.cropsAffected(dto.id),
                    disease.isDetectable(dto.id),
                    disease.offlineImages(dto.id),
                )
            }
        }
    }
}