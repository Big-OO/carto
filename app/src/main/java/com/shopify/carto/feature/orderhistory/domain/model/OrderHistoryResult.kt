package com.shopify.carto.feature.orderhistory.domain.model

sealed interface OrderHistoryResult<out T> {
    data class Success<T>(val data: T) : OrderHistoryResult<T>
    data class Failure(val failure: OrderHistoryFailure) : OrderHistoryResult<Nothing>
}
