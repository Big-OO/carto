package com.example.carto.feature.addresses.data.remote.service

import com.example.carto.feature.addresses.data.remote.dto.AddressResponseDto
import com.example.carto.feature.addresses.data.remote.dto.AddressesResponseDto
import com.example.carto.feature.addresses.data.remote.dto.CreateAddressBodyDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AddressesShopifyApi {
    @GET("admin/api/{version}/customers/{customerId}/addresses.json")
    suspend fun getAddresses(
        @Path("version") version: String,
        @Path("customerId") customerId: Long,
        @Query("limit") limit: Int = 100,
    ): Response<AddressesResponseDto>

    @POST("admin/api/{version}/customers/{customerId}/addresses.json")
    suspend fun createAddress(
        @Path("version") version: String,
        @Path("customerId") customerId: Long,
        @Body body: CreateAddressBodyDto,
    ): Response<AddressResponseDto>

    @PUT("admin/api/{version}/customers/{customerId}/addresses/{addressId}/default.json")
    suspend fun setDefaultAddress(
        @Path("version") version: String,
        @Path("customerId") customerId: Long,
        @Path("addressId") addressId: Long,
    ): Response<Unit>

    @DELETE("admin/api/{version}/customers/{customerId}/addresses/{addressId}.json")
    suspend fun deleteAddress(
        @Path("version") version: String,
        @Path("customerId") customerId: Long,
        @Path("addressId") addressId: Long,
    ): Response<Unit>
}
