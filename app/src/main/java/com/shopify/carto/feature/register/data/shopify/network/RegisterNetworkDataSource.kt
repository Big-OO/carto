package com.shopify.carto.feature.register.data.shopify.network

import com.shopify.carto.feature.register.data.shopify.model.CreateShopifyCustomerRequest
import com.shopify.carto.feature.register.data.shopify.model.ShopifyCustomerResponse
import com.shopify.carto.feature.register.data.shopify.model.ShopifyCustomersResponse
import retrofit2.Response

interface RegisterNetworkDataSource {
    suspend fun searchCustomerByEmail(
        version: String,
        query: String,
    ): Response<ShopifyCustomersResponse>

    suspend fun createCustomer(
        version: String,
        body: CreateShopifyCustomerRequest,
    ): Response<ShopifyCustomerResponse>
}
