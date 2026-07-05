package com.shopify.carto.feature.favorite.di


import com.shopify.carto.feature.favorite.data.local.FavoriteLocalDataSource
import com.shopify.carto.feature.favorite.data.local.FavoriteLocalDataSourceImpl
import com.shopify.carto.feature.favorite.data.repository.FavoriteRepositoryImpl
import com.shopify.carto.feature.favorite.domain.repository.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class FavoriteDIModule {
//
//    @Binds
//    @Singleton
//    abstract fun bindFavoriteRepository(repository: FavoriteRepositoryImpl): FavoriteRepository
//
//    @Binds
//    @Singleton
//    abstract fun bindFavoriteLocalDataSource(
//        impl: FavoriteLocalDataSourceImpl
//    ): FavoriteLocalDataSource
//
//
//}

import com.shopify.carto.feature.favorite.data.remote.FavoriteRemoteDataSource
import com.shopify.carto.feature.favorite.data.remote.FirestoreFavoriteRemoteDataSource


@Module
@InstallIn(SingletonComponent::class)
abstract class FavoriteDIModule {

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(repository: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteLocalDataSource(
        impl: FavoriteLocalDataSourceImpl
    ): FavoriteLocalDataSource

    @Binds
    @Singleton
    abstract fun bindFavoriteRemoteDataSource(
        impl: FirestoreFavoriteRemoteDataSource
    ): FavoriteRemoteDataSource
}