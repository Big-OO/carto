package com.example.carto.core.session.domain.usecase

import com.example.carto.core.session.domain.repository.AppSessionRepository
import javax.inject.Inject

class ObserveAppSessionUseCase @Inject constructor(
    private val repository: AppSessionRepository,
) {
    operator fun invoke() = repository.session
}
