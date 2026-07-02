package com.example.carto.feature.favorite.di


import com.example.carto.feature.favorite.data.local.FavoriteLocalDataSource
import com.example.carto.feature.favorite.data.local.FavoriteLocalDataSourceImpl
import com.example.carto.feature.favorite.data.repository.FavoriteRepositoryImpl
import com.example.carto.feature.favorite.domain.repository.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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


}