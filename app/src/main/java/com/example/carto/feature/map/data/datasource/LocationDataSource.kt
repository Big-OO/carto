package com.example.carto.feature.map.data.datasource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.example.carto.feature.map.data.result.MapDataResult
import com.example.carto.feature.map.domain.model.MapPoint
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
    @param:ApplicationContext private val context: Context,
) {
    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    suspend fun getCurrentLocation(timeoutMillis: Long = 10_000L): MapDataResult<MapPoint> {
        if (!hasLocationPermission()) {
            return MapDataResult.Failure("Location permission is not granted")
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
                MapDataResult.Failure("Current location returned null")
            }
        } catch (_: TimeoutCancellationException) {
            cancellationTokenSource.cancel()
            MapDataResult.Failure("Current location request timed out")
        } catch (throwable: Throwable) {
            MapDataResult.Failure(throwable.message)
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
}
