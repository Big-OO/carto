package com.example.carto.feature.profile.data.remote.api

import com.example.carto.feature.home.data.model.ProductsResponse
import com.example.carto.feature.profile.data.remote.dto.CustomerResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileService {
    @GET("admin/api/{version}/customers.json")
    suspend fun getCustomersByIds(
        @Path("version") version: String = "2026-01",
        @Query("ids") ids: String
    ): Response<CustomerResponseDto>
}