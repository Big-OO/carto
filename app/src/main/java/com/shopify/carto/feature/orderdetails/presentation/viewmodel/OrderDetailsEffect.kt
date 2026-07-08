package com.shopify.carto.feature.orderdetails.presentation.viewmodel

import com.shopify.carto.feature.orderdetails.presentation.state.OrderDetailsErrorType

sealed interface OrderDetailsEffect {
    data object NavigateBack : OrderDetailsEffect
    data class ShowError(val type: OrderDetailsErrorType) : OrderDetailsEffect
    data object ShowOrderCancelled : OrderDetailsEffect
    data object ShowOrderRemoved : OrderDetailsEffect
}
