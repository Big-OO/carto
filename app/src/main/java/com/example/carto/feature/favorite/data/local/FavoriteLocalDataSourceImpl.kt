package com.example.carto.feature.favorite.data.local


import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteLocalDataSourceImpl @Inject constructor(
    private val dao: FavoriteProductDao,
) : FavoriteLocalDataSource {

    override fun observeFavorites(): Flow<List<FavoriteProductEntity>> = dao.observeFavorites()

    override fun observeFavoriteIds(): Flow<List<Long>> = dao.observeFavoriteIds()

    override suspend fun insert(entity: FavoriteProductEntity) = dao.insert(entity)

    override suspend fun deleteById(productId: Long) = dao.deleteById(productId)

    override suspend fun exists(productId: Long): Boolean = dao.exists(productId)
}