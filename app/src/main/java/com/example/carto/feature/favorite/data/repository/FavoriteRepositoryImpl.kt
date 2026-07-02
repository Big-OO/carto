package com.example.carto.feature.favorite.data.repository

import com.example.carto.feature.favorite.data.mapper.toDomain
import com.example.carto.feature.favorite.data.mapper.toEntity
import com.example.carto.feature.favorite.domain.model.FavoriteProduct
import com.example.carto.feature.favorite.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.example.carto.feature.favorite.data.local.FavoriteLocalDataSource

class FavoriteRepositoryImpl @Inject constructor(
    private val localDataSource: FavoriteLocalDataSource,
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<FavoriteProduct>> =
        localDataSource.observeFavorites().map { entities -> entities.map { it.toDomain() } }

    override fun observeFavoriteIds(): Flow<Set<Long>> =
        localDataSource.observeFavoriteIds().map { it.toSet() }

    override suspend fun toggleFavorite(product: FavoriteProduct): Boolean {
        val alreadyFavorite = localDataSource.exists(product.productId)
        return if (alreadyFavorite) {
            localDataSource.deleteById(product.productId)
            false
        } else {
            localDataSource.insert(product.toEntity())
            true
        }
    }

    override suspend fun removeFavorite(productId: Long) {
        localDataSource.deleteById(productId)
    }
}