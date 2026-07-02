package com.example.carto.core.session.domain.usecase

import com.example.carto.core.session.domain.repository.AppSessionRepository

class FinishOnBoardingUseCase(private val repository: AppSessionRepository) {
    suspend  operator fun invoke() {
        repository.completeOnBoarding()
    }
}