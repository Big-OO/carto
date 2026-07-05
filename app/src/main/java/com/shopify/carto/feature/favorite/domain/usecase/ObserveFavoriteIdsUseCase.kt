package com.shopify.carto.feature.favorite.domain.usecase


import com.shopify.carto.feature.favorite.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteIdsUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(): Flow<Set<Long>> = repository.observeFavoriteIds()
}