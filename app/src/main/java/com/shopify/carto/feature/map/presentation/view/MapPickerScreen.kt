package com.shopify.carto.feature.map.presentation.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shopify.carto.R
import com.shopify.carto.feature.map.domain.model.MapPoint
import com.shopify.carto.feature.map.domain.model.SelectedMapAddress
import com.shopify.carto.feature.map.presentation.model.MapActionDialog
import com.shopify.carto.feature.map.presentation.view.components.MapActionRequiredDialog
import com.shopify.carto.feature.map.presentation.view.components.MapBoxContent
import com.shopify.carto.feature.map.presentation.view.components.MapSearchField
import com.shopify.carto.feature.map.presentation.view.components.openAppSettings
import com.shopify.carto.feature.map.presentation.view.components.openLocationSettings
import com.shopify.carto.feature.map.presentation.view.components.stringRes
import com.shopify.carto.feature.map.presentation.view.components.toMapboxPoint
import com.shopify.carto.feature.map.presentation.viewmodel.MapPickerViewModel
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MapPickerScreen(
    onBackClick: () -> Unit,
    onLocationSelected: (SelectedMapAddress) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: MapPickerViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val resources = LocalResources.current

    val snackbarHostState = remember { SnackbarHostState() }
    val mapViewportState = rememberMapViewportState {}

    fun flyTo(point: MapPoint, zoom: Double = 15.0) {
        mapViewportState.flyTo(
            cameraOptions = cameraOptions {
                center(point.toMapboxPoint())
                zoom(zoom)
                pitch(0.0)
                bearing(0.0)
            },
            animationOptions = MapAnimationOptions.mapAnimationOptions { duration(900) },
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

    LaunchedEffect(state.snackbarMessage) {
        state.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(resources.getString(message.stringRes()))
            viewModel.consumeSnackbar()
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
    ) {
        val screenHeight = maxHeight
        val appBarHeight = screenHeight * 0.15f

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
                .safeContentPadding(),
            verticalAlignment = Alignment.Top,
        ) {
            IconButton(
                onClick = onBackClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(appBarHeight * 0.4f)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.map_back_content_description),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.weight(0.05f))

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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp)
                .padding(bottom = 108.dp),
        )

        FloatingActionButton(
            onClick = viewModel::loadCurrentLocation,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 108.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = stringResource(R.string.map_my_location_content_description),
                modifier = Modifier.size(24.dp),
            )
        }

        AnimatedVisibility(
            visible = state.selectedAddress != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 28.dp),
        ) {
            Button(
                onClick = {
                    state.selectedAddress?.let(onLocationSelected)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp)),
                enabled = state.canSave,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                if (state.isResolvingAddress) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.map_use_this_location),
                        modifier = Modifier.padding(vertical = 10.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        if (state.isLoadingCurrentLocation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(enabled = false, onClick = {})
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularWavyProgressIndicator()
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
