package com.shopify.carto.feature.map.presentation.state

import com.shopify.carto.feature.map.domain.model.MapPoint
import com.shopify.carto.feature.map.domain.model.MapSearchSuggestion
import com.shopify.carto.feature.map.domain.model.SelectedMapAddress
import com.shopify.carto.feature.map.presentation.model.MapActionDialog
import com.shopify.carto.feature.map.presentation.model.MapSnackbarMessage

data class MapPickerUiState(
    val isLoadingCurrentLocation: Boolean = true,
    val isResolvingAddress: Boolean = false,
    val isSearching: Boolean = false,
    val query: String = "",
    val currentPoint: MapPoint? = null,
    val selectedAddress: SelectedMapAddress? = null,
    val suggestions: List<MapSearchSuggestion> = emptyList(),
    val showSuggestions: Boolean = false,
    val snackbarMessage: MapSnackbarMessage? = null,
    val actionDialog: MapActionDialog? = null,
) {
    val canSave: Boolean get() = selectedAddress != null && !isResolvingAddress
}
