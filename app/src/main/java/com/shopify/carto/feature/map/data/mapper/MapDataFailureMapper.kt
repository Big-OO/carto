package com.shopify.carto.feature.map.data.mapper

import com.shopify.carto.feature.map.data.error.GeocodingError
import com.shopify.carto.feature.map.data.error.LocationError
import com.shopify.carto.feature.map.data.error.SearchError
import com.shopify.carto.feature.map.data.result.MapDataResult
import com.shopify.carto.feature.map.domain.model.MapFailure
import com.shopify.carto.feature.map.domain.model.MapFailureType
import com.shopify.carto.feature.map.domain.model.MapResult


fun <D> MapDataResult.Failure<D, LocationError>.toMapLocationResult(): MapResult.Failure {
    return MapResult.Failure(
        failure = MapFailure(
            message = message,
            type = when (errorType) {
                LocationError.GPSDisabled -> MapFailureType.GPSDisabled
                LocationError.LocationPermissionDenied -> MapFailureType.LocationPermissionDenied
                LocationError.TimeOut -> MapFailureType.NetworkConnectionFailed
                LocationError.Unknown -> MapFailureType.Unknown
                null -> MapFailureType.Unknown
            }
        )
    )
}

fun <D> MapDataResult.Failure<D, GeocodingError>.toMapGeocodingResult(): MapResult.Failure {
    return MapResult.Failure(
        failure = MapFailure(
            message = message,
            type = when (errorType) {
                null,
                GeocodingError.GeocodingFailed -> MapFailureType.Unknown
            }
        )
    )
}

fun <D> MapDataResult.Failure<D, SearchError>.toMapSearchResult(): MapResult.Failure {
    return MapResult.Failure(
        failure = MapFailure(
            message = message,
            type = when (errorType) {
                null,
                SearchError.UnKnown -> MapFailureType.SearchFailed
            }
        )
    )
}
