package com.shopify.carto.feature.orderdetails.presentation.state

import com.shopify.carto.feature.orderdetails.presentation.model.OrderDetailsUi

data class OrderDetailsUiState(
    val isLoading: Boolean = true,
    val isProcessingAction: Boolean = false,
    val order: OrderDetailsUi? = null,
    val error: OrderDetailsErrorType? = null,
    val pendingDialog: OrderDetailsDialog? = null,
)

enum class OrderDetailsErrorType {
    Configuration,
    Unauthorized,
    Network,
    Server,
    NotFound,
    Unknown,
}

enum class OrderDetailsDialog {
    CancelOrder,
    HideOrder,
}
