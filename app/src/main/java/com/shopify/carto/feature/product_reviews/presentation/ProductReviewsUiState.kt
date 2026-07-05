package com.shopify.carto.feature.product_reviews.presentation

import com.shopify.carto.feature.product_reviews.domain.model.ProductReviews


data class ProductReviewsUiState(
    val isLoading: Boolean = false,
    val reviews: ProductReviews? = null,
    val errorMessage: String? = null
)