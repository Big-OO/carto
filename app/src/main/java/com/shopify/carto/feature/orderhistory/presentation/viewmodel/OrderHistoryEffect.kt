package com.shopify.carto.feature.orderhistory.presentation.viewmodel

sealed interface OrderHistoryEffect {
    data class NavigateToOrderDetails(val orderId: String) : OrderHistoryEffect
    data class ShowError(val type: com.shopify.carto.feature.orderhistory.presentation.state.OrderHistoryErrorType) : OrderHistoryEffect
}
