package com.example.carto.feature.map.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carto.R
import com.example.carto.feature.map.domain.model.MapPoint
import com.example.carto.feature.map.domain.model.SelectedMapAddress
import com.example.carto.feature.map.presentation.model.MapActionDialog
import com.example.carto.feature.map.presentation.view.components.MapActionRequiredDialog
import com.example.carto.feature.map.presentation.view.components.MapBoxContent
import com.example.carto.feature.map.presentation.view.components.MapSearchField
import com.example.carto.feature.map.presentation.view.components.openAppSettings
import com.example.carto.feature.map.presentation.view.components.openLocationSettings
import com.example.carto.feature.map.presentation.view.components.stringRes
import com.example.carto.feature.map.presentation.view.components.toMapboxPoint
import com.example.carto.feature.map.presentation.viewmodel.MapPickerViewModel
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions

@Composable
fun EmbeddedMapPicker(
    onAddressSelected: (SelectedMapAddress) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 300.dp,
) {
    val viewModel: MapPickerViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val mapViewportState = rememberMapViewportState {}
    var lastSelectedKey by remember { mutableStateOf<String?>(null) }

    fun flyTo(point: MapPoint, zoom: Double = 15.0) {
        mapViewportState.flyTo(
            cameraOptions = cameraOptions {
                center(point.toMapboxPoint())
                zoom(zoom)
                pitch(0.0)
                bearing(0.0)
            },
            animationOptions = MapAnimationOptions.mapAnimationOptions { duration(700) },
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.loadCurrentLocation()
        } else {
            viewModel.onLocationPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.loadCurrentLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(state.currentPoint) {
        state.currentPoint?.let { flyTo(it) }
    }

    LaunchedEffect(state.selectedAddress?.point) {
        state.selectedAddress?.point?.let { flyTo(it) }
    }

    LaunchedEffect(state.selectedAddress, state.isResolvingAddress) {
        val selected = state.selectedAddress ?: return@LaunchedEffect
        if (state.isResolvingAddress) return@LaunchedEffect
        val key = "${selected.point.latitude}:${selected.point.longitude}:${selected.address.addressLine}"
        if (key != lastSelectedKey) {
            lastSelectedKey = key
            onAddressSelected(selected)
        }
    }

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(context.getString(message.stringRes()))
            viewModel.consumeSnackbar()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            MapBoxContent(
                mapViewportState = mapViewportState,
                currentPoint = state.currentPoint,
                selectedPoint = state.selectedAddress?.point,
                onPointSelected = viewModel::selectPoint,
                modifier = Modifier.fillMaxSize(),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                MapSearchField(
                    query = state.query,
                    isSearching = state.isSearching,
                    suggestions = state.suggestions,
                    showSuggestions = state.showSuggestions,
                    onQueryChange = viewModel::onQueryChanged,
                    onClearClick = viewModel::clearSearch,
                    onSuggestionClick = viewModel::onSuggestionSelected,
                    modifier = Modifier.weight(1f),
                )
            }

            FloatingActionButton(
                onClick = viewModel::loadCurrentLocation,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = stringResource(R.string.map_my_location_content_description),
                    modifier = Modifier.size(22.dp),
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 76.dp),
            )

            if (state.isLoadingCurrentLocation || state.isResolvingAddress) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false, onClick = {})
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    state.actionDialog?.let { dialog ->
        MapActionRequiredDialog(
            dialog = dialog,
            onDismiss = viewModel::dismissActionDialog,
            onConfirm = {
                viewModel.dismissActionDialog()
                when (dialog) {
                    MapActionDialog.LocationPermission -> context.openAppSettings()
                    MapActionDialog.GpsDisabled -> context.openLocationSettings()
                }
            }
        )
    }
}
