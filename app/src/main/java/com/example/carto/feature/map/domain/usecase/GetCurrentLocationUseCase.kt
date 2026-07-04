package com.example.carto.feature.map.domain.usecase

import com.example.carto.feature.map.domain.repository.MapRepository
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val repository: MapRepository,
) {
    suspend operator fun invoke() = repository.getCurrentLocation()
}
