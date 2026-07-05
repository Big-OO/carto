package com.shopify.carto.feature.product_reviews.data.data_source

import com.shopify.carto.feature.product_reviews.data.dto.ProductReviewsResponseDto

interface ProductReviewsRemoteDataSource {
    suspend fun getProductReviews(productId: Long): Result<ProductReviewsResponseDto>
}