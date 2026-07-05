package com.shopify.carto.feature.favorite.data.remote

import com.shopify.carto.feature.favorite.data.remote.model.FavoriteRemoteModel
import kotlinx.coroutines.flow.Flow

interface FavoriteRemoteDataSource {
    fun observeFavorites(userId: String): Flow<List<FavoriteRemoteModel>>

    suspend fun getFavoritesOnce(userId: String): List<FavoriteRemoteModel>

    suspend fun upsert(userId: String, favorite: FavoriteRemoteModel)

    suspend fun delete(userId: String, productId: Long)
}
