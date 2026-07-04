package com.example.carto.feature.map.data.error

sealed interface LocationError {
    data object GPSDisabled: LocationError
    data object Unknown: LocationError
    data object LocationPermissionDenied: LocationError
    data object TimeOut: LocationError
}