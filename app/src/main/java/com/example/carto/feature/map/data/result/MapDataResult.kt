package com.example.carto.feature.map.data.result

sealed interface MapDataResult<out D, out E> {
    data class Success<D, E>(val data: D) : MapDataResult<D, E>
    data class Failure<D, E>(
        val message: String? = null,
        val errorType: E? = null,
    ) : MapDataResult<D, E>
}
