package com.example.carto.feature.map.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.example.carto.feature.map.data.datasource.AndroidGeocodingDataSource
import com.example.carto.feature.map.data.datasource.LocationDataSource
import com.example.carto.feature.map.data.datasource.MapboxSearchDataSource
import com.example.carto.feature.map.data.mapper.toMapGeocodingResult
import com.example.carto.feature.map.data.mapper.toMapLocationResult
import com.example.carto.feature.map.data.mapper.toMapSearchResult
import com.example.carto.feature.map.data.result.MapDataResult
import com.example.carto.feature.map.domain.model.MapAddress
import com.example.carto.feature.map.domain.model.MapPoint
import com.example.carto.feature.map.domain.model.MapResult
import com.example.carto.feature.map.domain.model.MapSearchSuggestion
import com.example.carto.feature.map.domain.repository.MapRepository
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource,
    private val searchDataSource: MapboxSearchDataSource,
    private val geocodingDataSource: AndroidGeocodingDataSource,
) : MapRepository {

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override suspend fun getCurrentLocation(): MapResult<MapPoint> {
        return when (val result = locationDataSource.getCurrentLocation()) {
            is MapDataResult.Success -> MapResult.Success(result.data)
            is MapDataResult.Failure -> result.toMapLocationResult()
        }
    }

    override suspend fun searchPlaces(query: String): MapResult<List<MapSearchSuggestion>> {
        return when (val result = searchDataSource.search(query)) {
            is MapDataResult.Success -> MapResult.Success(result.data)
            is MapDataResult.Failure -> result.toMapSearchResult()
        }
    }

    override suspend fun reverseGeocode(point: MapPoint): MapResult<MapAddress> {
        return when (val result = geocodingDataSource.reverseGeocode(point)) {
            is MapDataResult.Success -> MapResult.Success(result.data)
            is MapDataResult.Failure -> result.toMapGeocodingResult()
        }
    }
}
