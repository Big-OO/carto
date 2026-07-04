package com.example.carto.feature.map.di

import com.example.carto.feature.map.data.repository.MapRepositoryImpl
import com.example.carto.feature.map.domain.repository.MapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapDIModule {
    @Binds
    @Singleton
    abstract fun bindMapRepository(
        repository: MapRepositoryImpl,
    ): MapRepository
}
