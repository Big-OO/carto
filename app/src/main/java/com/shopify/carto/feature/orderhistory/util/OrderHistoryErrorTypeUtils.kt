package com.shopify.carto.feature.orderhistory.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.shopify.carto.R
import com.shopify.carto.feature.orderhistory.presentation.state.OrderHistoryErrorType


fun OrderHistoryErrorType.messageRes(): Int {
    return when (this) {
        OrderHistoryErrorType.MissingCustomer -> R.string.order_history_error_missing_customer
        OrderHistoryErrorType.Configuration -> R.string.order_history_error_configuration
        OrderHistoryErrorType.Unauthorized -> R.string.order_history_error_unauthorized
        OrderHistoryErrorType.Network -> R.string.order_history_error_network
        OrderHistoryErrorType.Server -> R.string.order_history_error_server
        OrderHistoryErrorType.NotFound -> R.string.order_history_error_not_found
        OrderHistoryErrorType.Unknown -> R.string.order_history_error_unknown
    }
}

@Composable
fun OrderHistoryErrorType.localizedMessage(): String {
    return when (this) {
        OrderHistoryErrorType.MissingCustomer -> stringResource(R.string.order_history_error_missing_customer)
        OrderHistoryErrorType.Configuration -> stringResource(R.string.order_history_error_configuration)
        OrderHistoryErrorType.Unauthorized -> stringResource(R.string.order_history_error_unauthorized)
        OrderHistoryErrorType.Network -> stringResource(R.string.order_history_error_network)
        OrderHistoryErrorType.Server -> stringResource(R.string.order_history_error_server)
        OrderHistoryErrorType.NotFound -> stringResource(R.string.order_history_error_not_found)
        OrderHistoryErrorType.Unknown -> stringResource(R.string.order_history_error_unknown)
    }
}
