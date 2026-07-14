package com.shopify.carto.feature.register.domain.model

sealed interface RegisterResult<out T> {
    data class Success<T>(val data: T) : RegisterResult<T>
    data class Failure(val failure: RegisterFailure) : RegisterResult<Nothing>
}
