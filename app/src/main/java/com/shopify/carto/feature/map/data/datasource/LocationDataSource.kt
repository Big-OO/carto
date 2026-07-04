package com.shopify.carto.feature.map.data.datasource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.shopify.carto.feature.map.data.error.LocationError
import com.shopify.carto.feature.map.data.result.MapDataResult
import com.shopify.carto.feature.map.domain.model.MapPoint
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(timeoutMillis: Long = 10_000L): MapDataResult<MapPoint, LocationError> {
        if (!hasLocationPermission()) {
            return MapDataResult.Failure(
                message = "Location permission is not granted",
                errorType = LocationError.LocationPermissionDenied,
            )
        }

        if (!isLocationEnabled()) {
            return MapDataResult.Failure(
                message = "Location services are disabled",
                errorType = LocationError.GPSDisabled,
            )
        }

        val cancellationTokenSource = CancellationTokenSource()

        return try {
            val location = withTimeout(timeoutMillis) {
                suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.token,
                    ).addOnSuccessListener { location ->
                        continuation.resume(location)
                    }.addOnFailureListener { _ ->
                        continuation.resume(null)
                    }

                    continuation.invokeOnCancellation {
                        cancellationTokenSource.cancel()
                    }
                }
            }

            if (location != null) {
                MapDataResult.Success(
                    MapPoint(
                        latitude = location.latitude,
                        longitude = location.longitude,
                    )
                )
            } else {
                MapDataResult.Failure("Current location returned null", LocationError.Unknown)
            }
        } catch (_: TimeoutCancellationException) {
            cancellationTokenSource.cancel()
            MapDataResult.Failure("Current location request timed out", LocationError.TimeOut)
        } catch (throwable: Throwable) {
            MapDataResult.Failure(throwable.message, LocationError.Unknown)
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

        return fine || coarse
    }

    private fun isLocationEnabled(): Boolean {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: Throwable) {
            false
        }
    }
}
