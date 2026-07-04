package com.shopify.carto.feature.profile.data.remote.datasource

import com.shopify.carto.feature.profile.data.remote.dto.ShopifyCustomerProfileResponseDto
import com.shopify.carto.feature.profile.data.remote.dto.UpdateShopifyCustomerRequestDto

interface ProfileRemoteDataSource {
    suspend fun getCustomerProfile(customerId: Long): Result<ShopifyCustomerProfileResponseDto>
    suspend fun updateCustomerProfile(
        customerId: Long,
        request: UpdateShopifyCustomerRequestDto
    ): Result<ShopifyCustomerProfileResponseDto>
}