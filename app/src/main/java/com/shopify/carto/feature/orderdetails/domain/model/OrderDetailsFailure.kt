package com.shopify.carto.feature.orderdetails.domain.model

data class OrderDetailsFailure(
    val type: OrderDetailsFailureType,
    val message: String,
)

enum class OrderDetailsFailureType {
    ShopifyConfigurationMissing,
    Unauthorized,
    Network,
    Server,
    NotFound,
    GraphQl,
    Unknown,
}

sealed interface OrderDetailsResult<out T> {
    data class Success<T>(val data: T) : OrderDetailsResult<T>
    data class Failure(val failure: OrderDetailsFailure) : OrderDetailsResult<Nothing>
}
