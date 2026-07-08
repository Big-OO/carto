package com.shopify.carto.feature.orderdetails.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.shopify.carto.R
import com.shopify.carto.feature.orderdetails.presentation.state.OrderDetailsErrorType

fun OrderDetailsErrorType.messageRes(): Int {
    return when (this) {
        OrderDetailsErrorType.Configuration -> R.string.order_details_error_configuration
        OrderDetailsErrorType.Unauthorized -> R.string.order_details_error_unauthorized
        OrderDetailsErrorType.Network -> R.string.order_details_error_network
        OrderDetailsErrorType.Server -> R.string.order_details_error_server
        OrderDetailsErrorType.NotFound -> R.string.order_details_error_not_found
        OrderDetailsErrorType.Unknown -> R.string.order_details_error_unknown
    }
}

@Composable
fun OrderDetailsErrorType.localizedMessage(): String {
    return when (this) {
        OrderDetailsErrorType.Configuration -> stringResource(R.string.order_details_error_configuration)
        OrderDetailsErrorType.Unauthorized -> stringResource(R.string.order_details_error_unauthorized)
        OrderDetailsErrorType.Network -> stringResource(R.string.order_details_error_network)
        OrderDetailsErrorType.Server -> stringResource(R.string.order_details_error_server)
        OrderDetailsErrorType.NotFound -> stringResource(R.string.order_details_error_not_found)
        OrderDetailsErrorType.Unknown -> stringResource(R.string.order_details_error_unknown)
    }
}
