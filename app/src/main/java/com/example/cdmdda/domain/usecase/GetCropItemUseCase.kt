package com.example.cdmdda.domain.usecase

import com.example.cdmdda.data.dto.CropItem
import com.example.cdmdda.presentation.helper.CropResourceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetCropItemUseCase(
    private val crop: CropResourceHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend operator fun invoke(id: String) = withContext(ioDispatcher) {
        return@withContext CropItem(
            id,
            crop.name(id),
            crop.isDiagnosable(id),
            crop.bannerId(id),
        )
    }
}