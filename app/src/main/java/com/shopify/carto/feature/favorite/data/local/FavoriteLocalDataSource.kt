package com.shopify.carto.feature.favorite.data.local


import kotlinx.coroutines.flow.Flow

interface FavoriteLocalDataSource {
    fun observeFavorites(userId: String): Flow<List<FavoriteProductEntity>>
    fun observeFavoriteIds(userId: String): Flow<List<Long>>
    suspend fun insert(entity: FavoriteProductEntity)
    suspend fun deleteById(productId: Long, userId: String)
    suspend fun exists(productId: Long, userId: String): Boolean
    suspend fun getAllOnce(userId: String): List<FavoriteProductEntity>
    suspend fun clearForUser(userId: String)
    suspend fun replaceForUser(userId: String, entities: List<FavoriteProductEntity>)
}