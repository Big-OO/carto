package com.shopify.carto.feature.login.di

import com.shopify.carto.feature.login.data.datasource.LoginRemoteDataSourceImpl
import com.shopify.carto.feature.login.data.repository.LoginRepositoryImpl
import com.shopify.carto.feature.login.domain.datasource.LoginRemoteDataSource
import com.shopify.carto.feature.login.domain.repository.LoginRepository
import com.shopify.carto.feature.login.domain.usecase.LoginUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        impl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindLoginRemoteDataSource(
        impl: LoginRemoteDataSourceImpl
    ): LoginRemoteDataSource
}
