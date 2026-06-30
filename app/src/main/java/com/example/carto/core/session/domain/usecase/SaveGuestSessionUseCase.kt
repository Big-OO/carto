package com.example.carto.core.session.domain.usecase

import com.example.carto.core.session.domain.repository.AppSessionRepository
import javax.inject.Inject

class SaveGuestSessionUseCase @Inject constructor(
    private val repository: AppSessionRepository,
) {
    suspend operator fun invoke() {
        repository.saveGuestSession()
    }
}
