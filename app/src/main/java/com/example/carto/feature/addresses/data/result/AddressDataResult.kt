package com.example.carto.feature.addresses.data.result

sealed interface AddressDataResult<out T, out E> {
    data class Success<T, E>(val data: T) : AddressDataResult<T, E>
    data class Failure<E>(
        val code: Int? = null,
        val error: E? = null,
        val message: String? = null,
    ) : AddressDataResult<Nothing, E>
}
