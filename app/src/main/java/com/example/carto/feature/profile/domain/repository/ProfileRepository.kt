package com.example.carto.feature.profile.domain.repository

import com.example.carto.feature.profile.domain.model.CustomerProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(customerId: Long): Flow<CustomerProfile?>
    suspend fun refreshProfile(customerId: Long): Result<Unit>
    suspend fun updateProfile(
        customerId: Long,
        firstName: String,
        lastName: String,
        email: String,
        phone: String?
    ): Result<Unit>
}