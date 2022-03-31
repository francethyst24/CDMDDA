package com.example.cdmdda.domain.usecase

import android.net.Uri
import com.example.cdmdda.data.repository.OnlineImageRepository

class GetOnlineImagesUseCase(
    private val imageRepository: OnlineImageRepository,
) {
    suspend operator fun invoke(id: String, onImageReceived: (Uri) -> Unit) {
        imageRepository.fetchOnlineImages(id) { onImageReceived(it) }
    }
}