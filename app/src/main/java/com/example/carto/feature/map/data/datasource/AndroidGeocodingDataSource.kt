package com.example.carto.feature.map.data.datasource

import android.content.Context
import android.location.Geocoder
import com.example.carto.feature.map.data.result.MapDataResult
import com.example.carto.feature.map.domain.model.MapAddress
import com.example.carto.feature.map.domain.model.MapPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

class AndroidGeocodingDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    suspend fun reverseGeocode(point: MapPoint): MapDataResult<MapAddress> {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val result = geocoder.getFromLocation(point.latitude, point.longitude, 1)
                    ?.firstOrNull()

                if (result == null) {
                    MapDataResult.Failure("No address found for selected point")
                } else {
                    MapDataResult.Success(
                        MapAddress(
                            addressLine = result.getAddressLine(0).orEmpty(),
                            city = result.locality ?: result.subAdminArea.orEmpty(),
                            province = result.adminArea.orEmpty(),
                            country = result.countryName.orEmpty(),
                            zip = result.postalCode.orEmpty(),
                        )
                    )
                }
            } catch (throwable: Throwable) {
                MapDataResult.Failure(throwable.message)
            }
        }
    }
}
