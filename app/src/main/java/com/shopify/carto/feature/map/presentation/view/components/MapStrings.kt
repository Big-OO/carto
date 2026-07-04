package com.shopify.carto.feature.map.presentation.view.components

import androidx.annotation.StringRes
import com.shopify.carto.R
import com.shopify.carto.feature.map.presentation.model.MapSnackbarMessage

@StringRes
fun MapSnackbarMessage.stringRes(): Int {
    return when (this) {
        MapSnackbarMessage.LocationUnavailable -> R.string.map_error_location_unavailable
        MapSnackbarMessage.LocationSelectedWithMissingDetails -> R.string.map_error_location_selected_missing_details
        MapSnackbarMessage.SearchFailed -> R.string.map_error_search_failed
        MapSnackbarMessage.AddressResolveFailed -> R.string.map_error_address_resolve_failed
        MapSnackbarMessage.Unknown -> R.string.map_error_unknown
    }
}
