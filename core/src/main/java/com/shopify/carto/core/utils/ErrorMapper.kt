package com.shopify.carto.core.utils

import androidx.annotation.StringRes
import com.shopify.carto.core.R
import com.shopify.carto.core.common.exception.DataException

@StringRes
fun Throwable.toUiErrorMessage(): Int {
    return when (this) {
        is DataException.NotFound -> R.string.error_product_not_found
        is DataException.Network -> R.string.error_network_connection
        is DataException.Server -> R.string.error_server_down
        else -> R.string.error_unknown
    }
}