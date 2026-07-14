package com.shopify.carto.feature.product_details.data.service

import com.shopify.carto.feature.product_details.data.dto.ProductDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductDetailsService {

    @GET("admin/api/{version}/products/{productId}.json")
    suspend fun getProductDetails(
        @Path("version") version: String = "2026-01",
        @Path("productId") productId: Long
    ): ProductDetailsResponse
}