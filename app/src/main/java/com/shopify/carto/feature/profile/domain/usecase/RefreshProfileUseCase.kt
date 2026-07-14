package com.shopify.carto.feature.profile.domain.usecase

import com.shopify.carto.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class RefreshProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(customerId: Long): Result<Unit> {
        return repository.refreshProfile(customerId)
    }
}
