package com.shopify.carto.feature.map.data.error

sealed interface GeocodingError {
    data object GeocodingFailed: GeocodingError
}