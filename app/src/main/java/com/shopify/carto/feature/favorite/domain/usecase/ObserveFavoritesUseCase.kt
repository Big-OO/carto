package com.shopify.carto.feature.favorite.domain.usecase

import com.shopify.carto.feature.favorite.domain.model.FavoriteProduct
import com.shopify.carto.feature.favorite.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    operator fun invoke(): Flow<List<FavoriteProduct>> = repository.observeFavorites()
}