package com.example.cdmdda.domain.usecase

import com.example.cdmdda.data.dto.DiseaseItemUiState
import com.example.cdmdda.presentation.helper.ResourceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetDiseaseItemUseCase(
    private val resources: ResourceHelper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    suspend operator fun invoke(id: String) = withContext(dispatcher) {
        DiseaseItemUiState(id, resources.fetchDiseaseIsSupported(id))
    }

}