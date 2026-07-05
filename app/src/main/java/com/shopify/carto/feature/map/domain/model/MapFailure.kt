package com.shopify.carto.feature.map.domain.model

enum class MapFailureType {
    GPSDisabled,
    LocationPermissionDenied,
    NetworkConnectionFailed,
    SearchFailed,
    Unknown,
}

data class MapFailure(
    val type: MapFailureType,
    val message: String? = null,
)
