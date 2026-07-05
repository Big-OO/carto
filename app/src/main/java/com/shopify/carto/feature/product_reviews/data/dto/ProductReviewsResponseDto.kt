package com.shopify.carto.feature.product_reviews.data.dto

import com.google.gson.annotations.SerializedName

data class ProductReviewsResponseDto(
    @SerializedName("metafields") val metafields: List<ProductReviewsDto>
)

data class ProductReviewsDto(
    @SerializedName("id") val id: Long,
    @SerializedName("namespace") val namespace: String,
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String,
    @SerializedName("created_at") val createdAt: String
)

data class ReviewValueDto(
    @SerializedName("rating") val rating: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)