package com.example.carto.search.domain.model

enum class SearchFailureType {
    Network,
    Unauthorized,
    Server,
    ShopifyConfigurationMissing,
    LocalStorage,
    Unknown,
}
