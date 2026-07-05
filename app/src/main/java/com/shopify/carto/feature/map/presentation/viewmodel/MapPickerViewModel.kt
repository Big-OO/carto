package com.shopify.carto.feature.map.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.map.domain.model.MapAddress
import com.shopify.carto.feature.map.domain.model.MapFailureType
import com.shopify.carto.feature.map.domain.model.MapPoint
import com.shopify.carto.feature.map.domain.model.MapResult
import com.shopify.carto.feature.map.domain.model.SelectedMapAddress
import com.shopify.carto.feature.map.domain.usecase.GetCurrentLocationUseCase
import com.shopify.carto.feature.map.domain.usecase.ReverseGeocodeUseCase
import com.shopify.carto.feature.map.domain.usecase.SearchMapPlacesUseCase
import com.shopify.carto.feature.map.presentation.model.MapActionDialog
import com.shopify.carto.feature.map.presentation.model.MapSnackbarMessage
import com.shopify.carto.feature.map.presentation.state.MapPickerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapPickerViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val searchMapPlacesUseCase: SearchMapPlacesUseCase,
    private val reverseGeocodeUseCase: ReverseGeocodeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MapPickerUiState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    fun loadCurrentLocation() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoadingCurrentLocation = true,
                    snackbarMessage = null,
                    actionDialog = null,
                )
            }
            when (val result = getCurrentLocationUseCase()) {
                is MapResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoadingCurrentLocation = false,
                            currentPoint = result.data,
                        )
                    }
                    selectPoint(result.data)
                }

                is MapResult.Failure -> {
                    Log.e(TAG, "Failed to load current location: ${result.failure.type}, ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isLoadingCurrentLocation = false,
                            snackbarMessage = result.failure.toSnackbarMessage(),
                            actionDialog = result.failure.toActionDialog(),
                        )
                    }
                }
            }
        }
    }

    fun onLocationPermissionMissing() {
        _state.update {
            it.copy(
                isLoadingCurrentLocation = false,
                snackbarMessage = null,
                actionDialog = MapActionDialog.LocationPermission,
            )
        }
    }

    fun onLocationPermissionDenied() {
        _state.update {
            it.copy(
                isLoadingCurrentLocation = false,
                snackbarMessage = null,
                actionDialog = MapActionDialog.LocationPermission,
            )
        }
    }

    fun onQueryChanged(query: String) {
        _state.update { it.copy(query = query) }
        searchJob?.cancel()

        if (query.isBlank()) {
            _state.update {
                it.copy(
                    isSearching = false,
                    suggestions = emptyList(),
                    showSuggestions = false,
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(350)
            _state.update { it.copy(isSearching = true, showSuggestions = true) }
            when (val result = searchMapPlacesUseCase(query)) {
                is MapResult.Success -> {
                    _state.update {
                        it.copy(
                            isSearching = false,
                            suggestions = result.data,
                            showSuggestions = result.data.isNotEmpty(),
                        )
                    }
                }

                is MapResult.Failure -> {
                    Log.e(TAG, "Map search failed: ${result.failure.type}, ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isSearching = false,
                            suggestions = emptyList(),
                            showSuggestions = false,
                            snackbarMessage = MapSnackbarMessage.SearchFailed,
                        )
                    }
                }
            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _state.update {
            it.copy(
                query = "",
                isSearching = false,
                suggestions = emptyList(),
                showSuggestions = false,
            )
        }
    }

    fun onSuggestionSelected(name: String, point: MapPoint?) {
        _state.update {
            it.copy(
                query = name,
                suggestions = emptyList(),
                showSuggestions = false,
            )
        }
        point?.let(::selectPoint)
    }

    fun selectPoint(point: MapPoint) {
        viewModelScope.launch {
            _state.update { it.copy(isResolvingAddress = true, snackbarMessage = null) }
            when (val result = reverseGeocodeUseCase(point)) {
                is MapResult.Success -> {
                    _state.update {
                        it.copy(
                            isResolvingAddress = false,
                            selectedAddress = SelectedMapAddress(point, result.data),
                        )
                    }
                }

                is MapResult.Failure -> {
                    Log.e(TAG, "Reverse geocoding failed: ${result.failure.type}, ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isResolvingAddress = false,
                            selectedAddress = SelectedMapAddress(point, MapAddress()),
                            snackbarMessage = MapSnackbarMessage.LocationSelectedWithMissingDetails,
                        )
                    }
                }
            }
        }
    }

    fun consumeSnackbar() {
        _state.update { it.copy(snackbarMessage = null) }
    }

    fun dismissActionDialog() {
        _state.update { it.copy(actionDialog = null) }
    }

    private fun com.shopify.carto.feature.map.domain.model.MapFailure.toSnackbarMessage(): MapSnackbarMessage? {
        return when (type) {
            MapFailureType.GPSDisabled,
            MapFailureType.LocationPermissionDenied -> null
            MapFailureType.NetworkConnectionFailed -> MapSnackbarMessage.LocationUnavailable
            MapFailureType.SearchFailed -> MapSnackbarMessage.SearchFailed
            MapFailureType.Unknown -> MapSnackbarMessage.Unknown
        }
    }

    private fun com.shopify.carto.feature.map.domain.model.MapFailure.toActionDialog(): MapActionDialog? {
        return when (type) {
            MapFailureType.GPSDisabled -> MapActionDialog.GpsDisabled
            MapFailureType.LocationPermissionDenied -> MapActionDialog.LocationPermission
            MapFailureType.NetworkConnectionFailed,
            MapFailureType.SearchFailed,
            MapFailureType.Unknown -> null
        }
    }

    companion object {
        private const val TAG = "MapPickerViewModel"
    }
}
