package com.shopify.carto.feature.search.di

import com.shopify.carto.core.network.qualifier.AdminRetrofit
import com.shopify.carto.feature.search.data.remote.SearchShopifyApi
import com.shopify.carto.feature.search.data.remote.network.RetrofitSearchNetworkDataSource
import com.shopify.carto.feature.search.data.remote.network.SearchNetworkDataSource
import com.shopify.carto.feature.search.data.remote.networkoperation.SearchNetworkOperation
import com.shopify.carto.feature.search.data.remote.networkoperation.StorefrontGraphQlSearchNetworkOperation
import com.shopify.carto.feature.search.data.repository.SearchRepositoryImpl
import com.shopify.carto.feature.search.domain.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
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

    @Binds
    @Singleton
    abstract fun bindSearchNetworkDataSource(
        dataSource: RetrofitSearchNetworkDataSource,
    ): SearchNetworkDataSource

    companion object {
        @Provides
        @Singleton
        fun provideSearchShopifyApi(@AdminRetrofit retrofit: Retrofit): SearchShopifyApi {
            return retrofit.create(SearchShopifyApi::class.java)
        }
    }
}
