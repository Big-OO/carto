package com.example.carto.feature.map.data.error

sealed interface GeocodingError {
    data object GeocodingFailed: GeocodingError
}