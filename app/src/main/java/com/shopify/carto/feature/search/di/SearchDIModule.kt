package com.shopify.carto.feature.search.di

import com.shopify.carto.feature.search.data.remote.SearchShopifyApi
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

    companion object {
        @Provides
        @Singleton
        fun provideSearchShopifyApi(retrofit: Retrofit): SearchShopifyApi {
            return retrofit.create(SearchShopifyApi::class.java)
        }
    }
}
