package com.shopify.carto.core.session.domain.usecase

import com.shopify.carto.core.session.domain.repository.AppSessionRepository
import javax.inject.Inject

class SaveAuthenticatedSessionUseCase @Inject constructor(
    private val repository: AppSessionRepository,
) {
    suspend operator fun invoke(customerId: String?) {
        repository.saveAuthenticatedSession(customerId)
    }
}
