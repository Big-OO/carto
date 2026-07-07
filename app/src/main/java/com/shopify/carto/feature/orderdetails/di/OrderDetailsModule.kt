package com.shopify.carto.feature.orderdetails.di

import com.shopify.carto.feature.orderdetails.data.remote.networkoperation.AdminGraphQlOrderDetailsNetworkOperation
import com.shopify.carto.feature.orderdetails.data.remote.networkoperation.OrderDetailsNetworkOperation
import com.shopify.carto.feature.orderdetails.data.remote.datasource.OrderDetailsRemoteDataSource
import com.shopify.carto.feature.orderdetails.data.remote.datasource.OrderDetailsRemoteDataSourceImpl
import com.shopify.carto.feature.orderdetails.data.repository.OrderDetailsRepositoryImpl
import com.shopify.carto.feature.orderdetails.domain.repository.OrderDetailsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OrderDetailsModule {

    @Binds
    @Singleton
    abstract fun bindOrderDetailsRepository(repository: OrderDetailsRepositoryImpl): OrderDetailsRepository

    @Binds
    @Singleton
    abstract fun bindOrderDetailsRemoteDataSource(dataSource: OrderDetailsRemoteDataSourceImpl): OrderDetailsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindOrderDetailsNetworkOperation(operation: AdminGraphQlOrderDetailsNetworkOperation): OrderDetailsNetworkOperation
}
