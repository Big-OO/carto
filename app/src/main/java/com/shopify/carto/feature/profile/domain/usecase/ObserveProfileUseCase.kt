package com.shopify.carto.feature.profile.domain.usecase

import com.shopify.carto.feature.profile.domain.model.CustomerProfile
import com.shopify.carto.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    operator fun invoke(customerId: Long): Flow<CustomerProfile?> {
        return repository.observeProfile(customerId)
    }
}
