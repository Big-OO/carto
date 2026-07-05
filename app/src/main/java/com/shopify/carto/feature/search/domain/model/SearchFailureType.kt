package com.shopify.carto.feature.search.domain.model

enum class SearchFailureType {
    Network,
    Unauthorized,
    Server,
    ShopifyConfigurationMissing,
    LocalStorage,
    Unknown,
}
