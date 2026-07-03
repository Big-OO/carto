package com.example.carto.feature.map.data.datasource

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.carto.feature.map.data.result.MapDataResult
import com.example.carto.feature.map.domain.model.MapAddress
import com.example.carto.feature.map.domain.model.MapPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class AndroidGeocodingDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    suspend fun reverseGeocode(point: MapPoint): MapDataResult<MapAddress> {
        val geocoder = Geocoder(context, Locale.getDefault())

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            reverseGeocodeApi33(
                geocoder = geocoder,
                point = point
            )
        } else {
            reverseGeocodeLegacy(
                geocoder = geocoder,
                point = point
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun reverseGeocodeApi33(
        geocoder: Geocoder,
        point: MapPoint,
    ): MapDataResult<MapAddress> {
        return suspendCancellableCoroutine { continuation ->
            try {
                geocoder.getFromLocation(
                    point.latitude,
                    point.longitude,
                    1,
                    object : Geocoder.GeocodeListener {

                        override fun onGeocode(addresses: MutableList<Address>) {
                            if (!continuation.isActive) return

                            continuation.resume(
                                addresses.toMapAddressResult()
                            ) { _, _, _ -> }
                        }

                        override fun onError(errorMessage: String?) {
                            if (!continuation.isActive) return

                            continuation.resume(
                                MapDataResult.Failure(
                                    errorMessage ?: "Failed to get address for selected point"
                                )
                            ) { _, _, _ -> }
                        }
                    }
                )
            } catch (throwable: Throwable) {
                if (continuation.isActive) {
                    continuation.resume(
                        MapDataResult.Failure(
                            throwable.message ?: "Failed to get address for selected point"
                        )
                    ) { _, _, _ -> }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun reverseGeocodeLegacy(
        geocoder: Geocoder,
        point: MapPoint,
    ): MapDataResult<MapAddress> {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(
                    point.latitude,
                    point.longitude,
                    1
                ).orEmpty()

                addresses.toMapAddressResult()
            } catch (throwable: Throwable) {
                MapDataResult.Failure(
                    throwable.message ?: "Failed to get address for selected point"
                )
            }
        }
    }

    private fun List<Address>.toMapAddressResult(): MapDataResult<MapAddress> {
        val result = firstOrNull()

        return if (result == null) {
            MapDataResult.Failure("No address found for selected point")
        } else {
            MapDataResult.Success(
                MapAddress(
                    addressLine = result.getAddressLine(0).orEmpty(),
                    city = result.locality
                        ?: result.subAdminArea.orEmpty(),
                    province = result.adminArea.orEmpty(),
                    country = result.countryName.orEmpty(),
                    zip = result.postalCode.orEmpty(),
                )
            )
        }
    }
}