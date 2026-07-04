package com.example.carto.feature.map.presentation.view.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.carto.R
import com.example.carto.feature.map.domain.model.MapPoint
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor

@Composable
fun MapBoxContent(
    mapViewportState: MapViewportState,
    currentPoint: MapPoint?,
    selectedPoint: MapPoint?,
    onPointSelected: (MapPoint) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val currentColor = Color(0xFF2196F3)

    val selectedMarker = rememberIconImage(
        key = R.drawable.ic_location_point,
        painter = painterResource(R.drawable.ic_location_point)
    )

    MapboxMap(
        modifier = modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        scaleBar = {},
        compass = {},
        logo = {},
        attribution = {},
        onMapClickListener = { point ->
            onPointSelected(
                MapPoint(
                    latitude = point.latitude(),
                    longitude = point.longitude(),
                )
            )
            true
        },
    ) {
        currentPoint?.let { point ->
            CircleAnnotation(point = point.toMapboxPoint()) {
                circleRadius = 9.0
                circleColor = currentColor
                circleStrokeWidth = 3.0
                circleStrokeColor = Color.White
                circleOpacity = 0.9
            }
        }

        selectedPoint?.let { point ->
            PointAnnotation(point = point.toMapboxPoint()) {
                iconImage = selectedMarker
                iconAnchor = IconAnchor.BOTTOM
                iconSize = 2.0
                iconColor = selectedColor
            }
        }
    }
}

fun MapPoint.toMapboxPoint(): Point = Point.fromLngLat(longitude, latitude)
