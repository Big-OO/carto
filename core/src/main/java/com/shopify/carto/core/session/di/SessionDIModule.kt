package com.shopify.carto.core.session.di

import com.shopify.carto.core.session.data.repository.AppSessionRepositoryImpl
import com.shopify.carto.core.session.domain.repository.AppSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionDIModule {
    @Binds
    @Singleton
    abstract fun bindAppSessionRepository(
        impl: AppSessionRepositoryImpl,
    ): AppSessionRepository
}
