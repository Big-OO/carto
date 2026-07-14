package com.shopify.carto.feature.profile.data.repository

import com.shopify.carto.feature.profile.data.local.CustomerProfileDao
import com.shopify.carto.feature.profile.data.mapper.toDomain
import com.shopify.carto.feature.profile.data.mapper.toEntity
import com.shopify.carto.feature.profile.data.remote.datasource.ProfileRemoteDataSource
import com.shopify.carto.feature.profile.data.remote.dto.UpdateShopifyCustomerDto
import com.shopify.carto.feature.profile.data.remote.dto.UpdateShopifyCustomerRequestDto
import com.shopify.carto.feature.profile.domain.model.CustomerProfile
import com.shopify.carto.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val localDataSource: CustomerProfileDao,
    private val remoteDataSource: ProfileRemoteDataSource,
) : ProfileRepository {

    override fun observeProfile(customerId: Long): Flow<CustomerProfile?> {
        return localDataSource.observeCustomerProfile(customerId.toString())
            .map { entity -> entity?.toDomain() }
    }

    override suspend fun refreshProfile(customerId: Long): Result<Unit> {
        return remoteDataSource.getCustomerProfile(customerId)
            .mapCatching { response ->
                val customerDto = response.customer ?: throw Exception("Customer not found in response")
                localDataSource.insertCustomerProfile(customerDto.toEntity())
            }
    }

    override suspend fun updateProfile(
        customerId: Long,
        firstName: String,
        lastName: String,
        email: String,
        phone: String?
    ): Result<Unit> {
        val request = UpdateShopifyCustomerRequestDto(
            customer = UpdateShopifyCustomerDto(
                id = customerId,
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone
            )
        )
        return remoteDataSource.updateCustomerProfile(customerId, request)
            .mapCatching { response ->
                val customerDto = response.customer ?: throw Exception("Customer not found in response")
                localDataSource.insertCustomerProfile(customerDto.toEntity())
            }
    }
}