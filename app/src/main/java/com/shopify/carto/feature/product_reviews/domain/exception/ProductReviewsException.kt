package com.shopify.carto.feature.product_reviews.domain.exception

sealed class ProductReviewsException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class NotFound(productId: Long) : ProductReviewsException("Reviews for product $productId were not found")

    class Network(cause: Throwable? = null) : ProductReviewsException("Unable to reach the server", cause)

    class Unauthorized : ProductReviewsException("Request was not authorized")

    class Unknown(cause: Throwable? = null) : ProductReviewsException("Something went wrong while fetching reviews", cause)
}