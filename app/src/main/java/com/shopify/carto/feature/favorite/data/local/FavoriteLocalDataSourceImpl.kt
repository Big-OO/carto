package com.shopify.carto.feature.favorite.data.local


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteLocalDataSourceImpl @Inject constructor(
    private val dao: FavoriteProductDao,
) : FavoriteLocalDataSource {

    override fun observeFavorites(userId: String): Flow<List<FavoriteProductEntity>> =
        dao.observeFavorites(userId)

    override fun observeFavoriteIds(userId: String): Flow<List<Long>> =
        dao.observeFavoriteIds(userId)

    override suspend fun insert(entity: FavoriteProductEntity) = dao.insert(entity)

    override suspend fun deleteById(productId: Long, userId: String) =
        dao.deleteById(productId, userId)

    override suspend fun exists(productId: Long, userId: String): Boolean =
        dao.exists(productId, userId)

    override suspend fun getAllOnce(userId: String): List<FavoriteProductEntity> =
        dao.getAllOnce(userId)

    override suspend fun clearForUser(userId: String) = dao.deleteAllForUser(userId)

    override suspend fun replaceForUser(userId: String, entities: List<FavoriteProductEntity>) =
        dao.replaceForUser(userId, entities)
}