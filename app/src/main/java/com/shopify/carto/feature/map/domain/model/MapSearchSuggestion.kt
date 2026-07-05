package com.shopify.carto.feature.map.domain.model

data class MapSearchSuggestion(
    val name: String,
    val address: String?,
    val point: MapPoint?,
)
