package com.example.carto.feature.profile.domain.usecase

import com.example.carto.feature.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(
        customerId: Long,
        firstName: String,
        lastName: String,
        email: String,
        phone: String?
    ): Result<Unit> {
        return repository.updateProfile(
            customerId = customerId,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone
        )
    }
}
