package com.shopify.carto.feature.register.data.shopify

import com.shopify.carto.feature.register.data.shopify.model.CreateShopifyCustomerRequest
import com.shopify.carto.feature.register.data.shopify.model.ShopifyCustomerResponse
import com.shopify.carto.feature.register.data.shopify.model.ShopifyCustomersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RegisterShopifyApi {
    @GET("admin/api/{version}/customers/search.json")
    suspend fun searchCustomerByEmail(
        @Path("version") version: String,
        @Query("query") query: String,
    ): Response<ShopifyCustomersResponse>

    @POST("admin/api/{version}/customers.json")
    suspend fun createCustomer(
        @Path("version") version: String,
        @Body body: CreateShopifyCustomerRequest,
    ): Response<ShopifyCustomerResponse>
}
