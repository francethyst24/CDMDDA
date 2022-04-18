package com.example.cdmdda.domain.usecase

import com.example.cdmdda.common.UriCallback
import com.example.cdmdda.data.repository.OnlineImageRepository

class GetOnlineImagesUseCase(
    private val imageRepository: OnlineImageRepository,
) {
    suspend operator fun invoke(id: String, onImageReceived: UriCallback) {
        imageRepository.fetchOnlineImages(id) { onImageReceived(it) }
    }
}