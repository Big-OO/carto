package com.shopify.carto.feature.profile.data.remote.network

import com.shopify.carto.feature.profile.data.remote.dto.ShopifyCustomerProfileResponseDto
import com.shopify.carto.feature.profile.data.remote.dto.UpdateShopifyCustomerRequestDto
import retrofit2.Response

interface ProfileNetworkDataSource {
    suspend fun getCustomerProfile(
        version: String,
        customerId: Long,
    ): Response<ShopifyCustomerProfileResponseDto>

    suspend fun updateCustomerProfile(
        version: String,
        customerId: Long,
        body: UpdateShopifyCustomerRequestDto,
    ): Response<ShopifyCustomerProfileResponseDto>
}
