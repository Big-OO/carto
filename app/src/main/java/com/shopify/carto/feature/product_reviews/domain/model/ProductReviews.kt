package com.shopify.carto.feature.product_reviews.domain.model

data class ProductReviews(
    val summary: ProductReviewsSummary,
    val reviews: List<ProductReview>
){
    data class ProductReviewsSummary(
        val averageRating: Double,
        val totalReviews: Int,
        val ratingDistribution: Map<Int, Float>
    )
    data class ProductReview(
        val id: Long,
        val rating: Int,
        val title: String,
        val body: String,
        val author: String,
        val date: String
    )
}

