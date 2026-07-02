package com.example.carto.feature.favorite.data.local


import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {
    fun observeFavorites(): Flow<List<FavoriteProductEntity>>
    fun observeFavoriteIds(): Flow<List<Long>>
    suspend fun insert(entity: FavoriteProductEntity)
    suspend fun deleteById(productId: Long)
    suspend fun exists(productId: Long): Boolean
}