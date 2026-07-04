package com.example.carto.feature.map.domain.usecase

import com.example.carto.feature.map.domain.model.MapPoint
import com.example.carto.feature.map.domain.repository.MapRepository
import javax.inject.Inject

class ReverseGeocodeUseCase @Inject constructor(
    private val repository: MapRepository,
) {
    suspend operator fun invoke(point: MapPoint) = repository.reverseGeocode(point)
}
