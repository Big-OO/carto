package com.example.carto.feature.favorite.domain.repository


import com.example.carto.feature.favorite.domain.model.FavoriteProduct
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<List<FavoriteProduct>>
    fun observeFavoriteIds(): Flow<Set<Long>>

    suspend fun toggleFavorite(product: FavoriteProduct): Boolean

    suspend fun removeFavorite(productId: Long)
}