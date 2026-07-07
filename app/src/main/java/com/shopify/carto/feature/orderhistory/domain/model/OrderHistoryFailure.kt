package com.shopify.carto.feature.orderhistory.domain.model

data class OrderHistoryFailure(
    val type: OrderHistoryFailureType,
    val message: String,
)

enum class OrderHistoryFailureType {
    MissingCustomer,
    ShopifyConfigurationMissing,
    Unauthorized,
    Network,
    Server,
    NotFound,
    GraphQl,
    Unknown,
}
