package com.shopify.carto.feature.register.data.shopify.network

import com.shopify.carto.feature.register.data.shopify.RegisterShopifyApi
import com.shopify.carto.feature.register.data.shopify.model.CreateShopifyCustomerRequest
import com.shopify.carto.feature.register.data.shopify.model.ShopifyCustomerResponse
import com.shopify.carto.feature.register.data.shopify.model.ShopifyCustomersResponse
import retrofit2.Response
import javax.inject.Inject

class RetrofitRegisterNetworkDataSource @Inject constructor(
    private val api: RegisterShopifyApi,
) : RegisterNetworkDataSource {

    override suspend fun searchCustomerByEmail(
        version: String,
        query: String,
    ): Response<ShopifyCustomersResponse> {
        return api.searchCustomerByEmail(version = version, query = query)
    }

    override suspend fun createCustomer(
        version: String,
        body: CreateShopifyCustomerRequest,
    ): Response<ShopifyCustomerResponse> {
        return api.createCustomer(version = version, body = body)
    }
}
