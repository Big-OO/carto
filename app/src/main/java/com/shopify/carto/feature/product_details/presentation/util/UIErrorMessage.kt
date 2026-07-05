package com.shopify.carto.feature.product_details.presentation.util


import androidx.annotation.StringRes
import com.shopify.carto.R
import com.shopify.carto.feature.product_details.data.exception.ProductDetailsException

@StringRes
fun Throwable.toUiErrorMessage(): Int {
    return when (this) {
        is ProductDetailsException.NotFound -> R.string.error_product_not_found
        is ProductDetailsException.Network -> R.string.error_network_connection
        is ProductDetailsException.Server -> R.string.error_server_down
        else -> R.string.error_unknown
    }
}