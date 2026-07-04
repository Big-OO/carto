package com.shopify.carto.feature.map.domain.model

data class MapAddress(
    val addressLine: String = "",
    val city: String = "",
    val province: String = "",
    val country: String = "",
    val zip: String = "",
)
