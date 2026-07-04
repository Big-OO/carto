package com.example.carto.feature.profile.data.remote.api

import com.example.carto.feature.profile.data.remote.dto.ShopifyCustomerProfileResponseDto
import com.example.carto.feature.profile.data.remote.dto.UpdateShopifyCustomerRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileShopifyApi {
    @GET("admin/api/{version}/customers/{customer_id}.json")
    suspend fun getCustomerProfile(
        @Path("version") version: String,
        @Path("customer_id") customerId: Long,
    ): Response<ShopifyCustomerProfileResponseDto>

    @PUT("admin/api/{version}/customers/{customer_id}.json")
    suspend fun updateCustomerProfile(
        @Path("version") version: String,
        @Path("customer_id") customerId: Long,
        @Body body: UpdateShopifyCustomerRequestDto,
    ): Response<ShopifyCustomerProfileResponseDto>
}
