package com.shopify.carto.feature.product_details.data.exception


sealed class ProductDetailsException(cause: Throwable? = null) : Exception(cause) {
    class NotFound(val productId: Long) : ProductDetailsException()
    class Server(cause: Throwable) : ProductDetailsException(cause)
    class Network(cause: Throwable) : ProductDetailsException(cause)
    class Unknown(cause: Throwable) : ProductDetailsException(cause)
}