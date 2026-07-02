package com.example.carto.feature.map.domain.model

enum class MapFailureType {
    PermissionDenied,
    LocationUnavailable,
    SearchFailed,
    GeocodingFailed,
    Unknown,
}

data class MapFailure(
    val type: MapFailureType,
    val developerMessage: String? = null,
)
