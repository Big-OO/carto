package com.shopify.carto.feature.profile.data.remote.network

import com.shopify.carto.feature.profile.data.remote.api.ProfileShopifyApi
import com.shopify.carto.feature.profile.data.remote.dto.ShopifyCustomerProfileResponseDto
import com.shopify.carto.feature.profile.data.remote.dto.UpdateShopifyCustomerRequestDto
import retrofit2.Response
import javax.inject.Inject

class RetrofitProfileNetworkDataSource @Inject constructor(
    private val api: ProfileShopifyApi,
) : ProfileNetworkDataSource {

    override suspend fun getCustomerProfile(
        version: String,
        customerId: Long,
    ): Response<ShopifyCustomerProfileResponseDto> {
        return api.getCustomerProfile(version = version, customerId = customerId)
    }

    override suspend fun updateCustomerProfile(
        version: String,
        customerId: Long,
        body: UpdateShopifyCustomerRequestDto,
    ): Response<ShopifyCustomerProfileResponseDto> {
        return api.updateCustomerProfile(version = version, customerId = customerId, body = body)
    }
}
