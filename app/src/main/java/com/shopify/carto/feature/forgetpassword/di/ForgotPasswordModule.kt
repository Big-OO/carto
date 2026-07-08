package com.shopify.carto.feature.forgetpassword.di

import com.shopify.carto.feature.forgetpassword.data.datasource.ForgotPasswordRemoteDataSource
import com.shopify.carto.feature.forgetpassword.data.datasource.ForgotPasswordRemoteDataSourceImpl
import com.shopify.carto.feature.forgetpassword.data.repository.ForgotPasswordRepositoryImpl
import com.shopify.carto.feature.forgetpassword.domain.repository.ForgotPasswordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ForgotPasswordModule {

    @Binds
    @Singleton
    abstract fun bindForgotPasswordRemoteDataSource(
        impl: ForgotPasswordRemoteDataSourceImpl
    ): ForgotPasswordRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindForgotPasswordRepository(
        impl: ForgotPasswordRepositoryImpl
    ): ForgotPasswordRepository
}
