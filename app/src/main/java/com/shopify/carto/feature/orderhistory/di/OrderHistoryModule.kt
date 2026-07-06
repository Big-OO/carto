package com.shopify.carto.feature.orderhistory.di

import com.shopify.carto.core.network.qualifier.AdminRetrofit
import com.shopify.carto.feature.orderhistory.data.remote.api.OrderHistoryShopifyGraphQlApi
import com.shopify.carto.feature.orderhistory.data.remote.network.AdminGraphQlOrderHistoryNetworkDataSource
import com.shopify.carto.feature.orderhistory.data.remote.network.OrderHistoryNetworkDataSource
import com.shopify.carto.feature.orderhistory.data.repository.OrderHistoryRepositoryImpl
import com.shopify.carto.feature.orderhistory.domain.repository.OrderHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderHistoryModule {

    @Binds
    @Singleton
    abstract fun bindOrderHistoryRepository(repository: OrderHistoryRepositoryImpl): OrderHistoryRepository

    @Binds
    @Singleton
    abstract fun bindOrderHistoryNetworkDataSource(dataSource: AdminGraphQlOrderHistoryNetworkDataSource): OrderHistoryNetworkDataSource

    companion object {
        @Provides
        @Singleton
        fun provideOrderHistoryShopifyGraphQlApi(@AdminRetrofit retrofit: Retrofit): OrderHistoryShopifyGraphQlApi {
            return retrofit.create(OrderHistoryShopifyGraphQlApi::class.java)
        }
    }
}
