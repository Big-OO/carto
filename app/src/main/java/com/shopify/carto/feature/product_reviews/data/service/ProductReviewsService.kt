package com.shopify.carto.feature.product_reviews.data.service

import com.shopify.carto.feature.product_reviews.data.dto.ProductReviewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductReviewsService {

    @GET("admin/api/{version}/products/{productId}/metafields.json")
    suspend fun getProductReviews(
        @Path("productId") productId: Long,
        @Path("version") version: String = "2026-01",
        @Query("namespace") namespace: String = "reviews"
    ): ProductReviewsResponseDto
}