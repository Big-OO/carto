package com.example.carto.feature.brand.data.remote.api

import com.example.carto.feature.brand.data.remote.dto.BrandProductsResponseDto
import com.example.carto.feature.brand.data.remote.dto.BrandsResponseDto
import com.example.carto.feature.search.data.remote.model.SearchProductsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BrandsApiService {

    @GET("admin/api/{version}/smart_collections.json")
    suspend fun getBrands(
        @Path("version") version: String,
        @Query("limit") limit: Int = 50
    ): Response<BrandsResponseDto>

    @GET("admin/api/{version}/products.json")
    suspend fun getProductsByBrand(
        @Path("version") version: String,
        @Query("vendor") vendor: String,
        @Query("limit") limit: Int = 50
    ): Response<BrandProductsResponseDto>
}

