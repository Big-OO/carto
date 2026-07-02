package com.example.carto.feature.home.di

import com.example.carto.feature.home.data.HomeApiService
import com.example.carto.feature.home.data.remote.HomeRemoteDataSource
import com.example.carto.feature.home.data.remote.HomeRemoteDataSourceImpl
import com.example.carto.feature.home.data.repository.HomeRepositoryImp
import com.example.carto.feature.home.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeDIModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(repository: HomeRepositoryImp): HomeRepository

    companion object {
        @Provides
        @Singleton
        fun provideHomeApiService(retrofit: Retrofit): HomeApiService {
            return retrofit.create(HomeApiService::class.java)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindHomeRemoteDataSource(
        impl: HomeRemoteDataSourceImpl
    ): HomeRemoteDataSource
}