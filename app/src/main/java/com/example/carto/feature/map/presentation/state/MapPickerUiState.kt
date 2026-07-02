package com.example.carto.feature.map.presentation.state

import com.example.carto.feature.map.domain.model.MapPoint
import com.example.carto.feature.map.domain.model.MapSearchSuggestion
import com.example.carto.feature.map.domain.model.SelectedMapAddress

data class MapPickerUiState(
    val isLoadingCurrentLocation: Boolean = true,
    val isResolvingAddress: Boolean = false,
    val isSearching: Boolean = false,
    val query: String = "",
    val currentPoint: MapPoint? = null,
    val selectedAddress: SelectedMapAddress? = null,
    val suggestions: List<MapSearchSuggestion> = emptyList(),
    val showSuggestions: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSave: Boolean get() = selectedAddress != null && !isResolvingAddress
}
