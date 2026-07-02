package com.example.carto.feature.map.domain.usecase

import com.example.carto.feature.map.domain.repository.MapRepository
import javax.inject.Inject

class SearchMapPlacesUseCase @Inject constructor(
    private val repository: MapRepository,
) {
    suspend operator fun invoke(query: String) = repository.searchPlaces(query)
}
