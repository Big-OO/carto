package com.shopify.carto.feature.map.domain.model

sealed interface MapResult<out T> {
    data class Success<T>(val data: T) : MapResult<T>
    data class Failure(val failure: MapFailure) : MapResult<Nothing>
}
