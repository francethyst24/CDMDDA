package com.example.cdmdda.domain.usecase

import com.example.cdmdda.data.dto.DiseaseItem
import com.example.cdmdda.presentation.helper.DiseaseResourceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetDiseaseItemUseCase(
    private val disease: DiseaseResourceHelper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(id: String) = withContext(dispatcher) {
        DiseaseItem(id, disease.isDetectable(id))
    }

}