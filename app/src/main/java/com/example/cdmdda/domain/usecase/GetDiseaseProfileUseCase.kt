package com.example.cdmdda.domain.usecase

import com.example.cdmdda.data.dto.*
import com.example.cdmdda.presentation.helper.DiseaseResourceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetDiseaseProfileUseCase(
    private val disease: DiseaseResourceHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(dto: DiseaseUiState) = withContext(ioDispatcher) {
        return@withContext when (dto) {
            is DiseaseItemUiState -> {
                DiseaseProfileUiState(
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
            is DiseaseDiagnosisUiState, is DiseaseTextUiState -> {
                DiseaseProfileUiState(
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