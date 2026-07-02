package com.example.carto.feature.addresses.data.result

sealed interface AddressDataResult<out T> {
    data class Success<T>(val data: T) : AddressDataResult<T>
    data class Failure(
        val code: Int? = null,
        val developerMessage: String? = null,
    ) : AddressDataResult<Nothing>
}
