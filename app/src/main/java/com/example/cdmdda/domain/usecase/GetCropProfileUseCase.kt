package com.example.cdmdda.domain.usecase

import com.example.cdmdda.data.dto.Crop
import com.example.cdmdda.data.dto.CropItem
import com.example.cdmdda.data.dto.CropProfile
import com.example.cdmdda.data.dto.CropText
import com.example.cdmdda.data.repository.CropDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetCropProfileUseCase(
    private val crop: CropDataRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    suspend operator fun invoke(dto: Crop) = withContext(defaultDispatcher) {
        return@withContext when (dto) {
            is CropItem -> {
                CropProfile(
                    dto.name,
                    crop.sciName(dto.id),
                    crop.description(dto.id),
                    crop.diseasesAcquirable(dto.id),
                    dto.isDiagnosable,
                    dto.bannerId,
                )
            }
            is CropText -> {
                CropProfile(
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