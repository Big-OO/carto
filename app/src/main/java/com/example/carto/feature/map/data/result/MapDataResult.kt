package com.example.carto.feature.map.data.result

sealed interface MapDataResult<out T> {
    data class Success<T>(val data: T) : MapDataResult<T>
    data class Failure(
        val developerMessage: String? = null,
    ) : MapDataResult<Nothing>
}
