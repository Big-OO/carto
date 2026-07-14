package com.shopify.carto.feature.profile.di

import com.shopify.carto.feature.profile.data.repository.ProfileRepositoryImpl
import com.shopify.carto.feature.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {
    @Binds
    @Singleton
    abstract fun bindProfileRepository(repository: ProfileRepositoryImpl): ProfileRepository
}