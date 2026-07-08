package com.shopify.carto.feature.orderhistory.presentation.state

import com.shopify.carto.feature.orderhistory.presentation.model.OrderHistoryItemUi
import com.shopify.carto.feature.orderhistory.presentation.model.OrderHistoryTabUi

data class OrderHistoryUiState(
    val isLoading: Boolean = false,
    val selectedTab: OrderHistoryTabUi = OrderHistoryTabUi.Ongoing,
    val orders: List<OrderHistoryItemUi> = emptyList(),
    val error: OrderHistoryErrorType? = null,
) {
    val visibleOrders: List<OrderHistoryItemUi>
        get() = orders.filter { it.tab == selectedTab }
}

enum class OrderHistoryErrorType {
    MissingCustomer,
    Configuration,
    Unauthorized,
    Network,
    Server,
    NotFound,
    Unknown,
}
