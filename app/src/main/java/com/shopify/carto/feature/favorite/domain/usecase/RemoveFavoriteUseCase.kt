package com.shopify.carto.feature.favorite.domain.usecase

import com.shopify.carto.feature.favorite.domain.repository.FavoriteRepository
import javax.inject.Inject

class RemoveFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository,
) {
    suspend operator fun invoke(productId: Long) = repository.removeFavorite(productId)
}