package com.example.carto.feature.map.domain.repository

import com.example.carto.feature.map.domain.model.MapAddress
import com.example.carto.feature.map.domain.model.MapPoint
import com.example.carto.feature.map.domain.model.MapResult
import com.example.carto.feature.map.domain.model.MapSearchSuggestion

interface MapRepository {
    suspend fun getCurrentLocation(): MapResult<MapPoint>
    suspend fun searchPlaces(query: String): MapResult<List<MapSearchSuggestion>>
    suspend fun reverseGeocode(point: MapPoint): MapResult<MapAddress>
}
