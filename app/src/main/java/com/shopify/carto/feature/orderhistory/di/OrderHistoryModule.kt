package com.shopify.carto.feature.orderhistory.di

import com.shopify.carto.feature.orderhistory.data.remote.networkoperation.AdminGraphQlOrderHistoryNetworkOperation
import com.shopify.carto.feature.orderhistory.data.remote.networkoperation.OrderHistoryNetworkOperation
import com.shopify.carto.feature.orderhistory.data.remote.datasource.OrderHistoryRemoteDataSource
import com.shopify.carto.feature.orderhistory.data.remote.datasource.OrderHistoryRemoteDataSourceImpl
import com.shopify.carto.feature.orderhistory.data.repository.OrderHistoryRepositoryImpl
import com.shopify.carto.feature.orderhistory.domain.repository.OrderHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderHistoryModule {

    @Binds
    @Singleton
    abstract fun bindOrderHistoryRepository(repository: OrderHistoryRepositoryImpl): OrderHistoryRepository

    @Binds
    @Singleton
    abstract fun bindOrderHistoryRemoteDataSource(dataSource: OrderHistoryRemoteDataSourceImpl): OrderHistoryRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindOrderHistoryNetworkOperation(operation: AdminGraphQlOrderHistoryNetworkOperation): OrderHistoryNetworkOperation
}
