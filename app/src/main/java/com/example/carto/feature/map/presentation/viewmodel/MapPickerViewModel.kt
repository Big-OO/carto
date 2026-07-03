package com.example.carto.feature.map.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.feature.map.domain.model.MapPoint
import com.example.carto.feature.map.domain.model.MapResult
import com.example.carto.feature.map.domain.model.SelectedMapAddress
import com.example.carto.feature.map.domain.usecase.GetCurrentLocationUseCase
import com.example.carto.feature.map.domain.usecase.ReverseGeocodeUseCase
import com.example.carto.feature.map.domain.usecase.SearchMapPlacesUseCase
import com.example.carto.feature.map.presentation.state.MapPickerUiState
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
            _state.update { it.copy(isLoadingCurrentLocation = true, errorMessage = null) }
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
                    Log.e(TAG, "Failed to load current location: ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isLoadingCurrentLocation = false,
                            errorMessage = "We couldn't get your location. Select a point manually or try again.",
                        )
                    }
                }
            }
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
                    Log.e(TAG, "Map search failed: ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isSearching = false,
                            suggestions = emptyList(),
                            showSuggestions = false,
                            errorMessage = "We couldn't search for this address. Try again.",
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
            _state.update { it.copy(isResolvingAddress = true, errorMessage = null) }
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
                    Log.e(TAG, "Reverse geocoding failed: ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isResolvingAddress = false,
                            selectedAddress = SelectedMapAddress(point, com.example.carto.feature.map.domain.model.MapAddress()),
                            errorMessage = "Location selected. Add missing address details manually.",
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MapPickerViewModel"
    }
}
