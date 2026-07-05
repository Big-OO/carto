package com.shopify.carto.feature.addresses.domain.model

sealed interface AddressResult<out T> {
    data class Success<T>(val data: T) : AddressResult<T>
    data class Failure(val failure: AddressFailure) : AddressResult<Nothing>
}
