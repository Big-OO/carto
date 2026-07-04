package com.shopify.carto.feature.profile.data.remote.datasource

import com.shopify.carto.core.config.ShopifyConfig
import com.shopify.carto.feature.profile.data.remote.api.ProfileShopifyApi
import com.shopify.carto.feature.profile.data.remote.dto.ShopifyCustomerProfileResponseDto
import com.shopify.carto.feature.profile.data.remote.dto.UpdateShopifyCustomerRequestDto
import javax.inject.Inject

class ProfileRemoteDataSourceImpl @Inject constructor(
    private val api: ProfileShopifyApi,
    private val config: ShopifyConfig,
) : ProfileRemoteDataSource {

    override suspend fun getCustomerProfile(customerId: Long): Result<ShopifyCustomerProfileResponseDto> {
        return runCatching {
            val response = api.getCustomerProfile(
                version = config.apiVersion,
                customerId = customerId
            )
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Response body was null")
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                throw Exception("API Error ${response.code()}: $errorMsg")
            }
        }
    }

    override suspend fun updateCustomerProfile(
        customerId: Long,
        request: UpdateShopifyCustomerRequestDto
    ): Result<ShopifyCustomerProfileResponseDto> {
        return runCatching {
            val response = api.updateCustomerProfile(
                version = config.apiVersion,
                customerId = customerId,
                body = request
            )
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Response body was null")
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                throw Exception("API Error ${response.code()}: $errorMsg")
            }
        }
    }
}