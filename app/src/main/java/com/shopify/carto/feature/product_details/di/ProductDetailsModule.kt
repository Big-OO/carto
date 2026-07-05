package com.shopify.carto.feature.product_details.di

import com.shopify.carto.core.network.qualifier.AdminRetrofit
import com.shopify.carto.feature.product_details.data.datasource.remote.ProductDetailsRemoteDataSource
import com.shopify.carto.feature.product_details.data.datasource.remote.ProductDetailsRemoteDataSourceImpl
import com.shopify.carto.feature.product_details.data.service.ProductDetailsService
import com.shopify.carto.feature.product_details.data.repository.ProductDetailsRepositoryImpl
import com.shopify.carto.feature.product_details.domain.repository.ProductDetailsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDetailsModule {

    @Binds
    abstract fun bindProductDetailsRepository(
        implementation: ProductDetailsRepositoryImpl
    ): ProductDetailsRepository

    @Binds
    abstract fun bindProductDetailsDataSource(
        impl: ProductDetailsRemoteDataSourceImpl
    ): ProductDetailsRemoteDataSource


    companion object {

        @Provides
        @Singleton
        fun provideProductDetailsService(
            @AdminRetrofit retrofit: Retrofit): ProductDetailsService {
            return retrofit.create(ProductDetailsService::class.java)
        }
    }
}