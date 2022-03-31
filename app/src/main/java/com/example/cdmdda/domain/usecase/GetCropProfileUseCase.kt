package com.example.cdmdda.domain.usecase

import com.example.cdmdda.data.dto.CropItemUiState
import com.example.cdmdda.data.dto.CropProfileUiState
import com.example.cdmdda.data.dto.CropTextUiState
import com.example.cdmdda.data.dto.CropUiState
import com.example.cdmdda.presentation.helper.CropResourceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetCropProfileUseCase(
    private val crop: CropResourceHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(dto: CropUiState) = withContext(ioDispatcher) {
        return@withContext when (dto) {
            is CropItemUiState -> {
                CropProfileUiState(
                    dto.name,
                    crop.sciName(dto.id),
                    crop.description(dto.id),
                    crop.diseasesAcquirable(dto.id),
                    dto.isDiagnosable,
                    dto.bannerId,
                )
            }
            is CropTextUiState -> {
                CropProfileUiState(
                    dto.name,
                    crop.sciName(dto.id),
                    crop.description(dto.id),
                    crop.diseasesAcquirable(dto.id),
                    crop.isDiagnosable(dto.id),
                    crop.bannerId(dto.id),
                )
            }
        }
    }
}