package com.shopify.carto.feature.search.di

import com.shopify.carto.feature.search.data.remote.networkoperation.SearchNetworkOperation
import com.shopify.carto.feature.search.data.remote.networkoperation.StorefrontGraphQlSearchNetworkOperation
import com.shopify.carto.feature.search.data.repository.SearchRepositoryImpl
import com.shopify.carto.feature.search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchDIModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(repository: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    abstract fun bindSearchNetworkOperation(
        operation: StorefrontGraphQlSearchNetworkOperation,
    ): SearchNetworkOperation
}
