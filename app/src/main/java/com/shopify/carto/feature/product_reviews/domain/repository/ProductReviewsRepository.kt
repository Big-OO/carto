package com.shopify.carto.feature.product_reviews.domain.repository

import com.shopify.carto.feature.product_reviews.domain.model.ProductReviews

interface ProductReviewsRepository {

    suspend fun getProductReviews(productId: Long): Result<ProductReviews>
}