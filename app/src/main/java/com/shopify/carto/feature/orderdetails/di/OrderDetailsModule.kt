package com.shopify.carto.feature.orderdetails.di

import com.shopify.carto.core.network.qualifier.AdminRetrofit
import com.shopify.carto.feature.orderdetails.data.remote.api.OrderDetailsShopifyGraphQlApi
import com.shopify.carto.feature.orderdetails.data.remote.network.AdminGraphQlOrderDetailsNetworkDataSource
import com.shopify.carto.feature.orderdetails.data.remote.network.OrderDetailsNetworkDataSource
import com.shopify.carto.feature.orderdetails.data.repository.OrderDetailsRepositoryImpl
import com.shopify.carto.feature.orderdetails.domain.repository.OrderDetailsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderDetailsModule {

    @Binds
    @Singleton
    abstract fun bindOrderDetailsRepository(repository: OrderDetailsRepositoryImpl): OrderDetailsRepository

    @Binds
    @Singleton
    abstract fun bindOrderDetailsNetworkDataSource(dataSource: AdminGraphQlOrderDetailsNetworkDataSource): OrderDetailsNetworkDataSource

    companion object {
        @Provides
        @Singleton
        fun provideOrderDetailsShopifyGraphQlApi(@AdminRetrofit retrofit: Retrofit): OrderDetailsShopifyGraphQlApi {
            return retrofit.create(OrderDetailsShopifyGraphQlApi::class.java)
        }
    }
}
