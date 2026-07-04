package com.shopify.carto.feature.brand.di

import com.shopify.carto.feature.brand.data.remote.api.BrandsApiService
import com.shopify.carto.feature.brand.data.remote.repository.BrandRepositoryImpl
import com.shopify.carto.feature.brand.domain.repository.BrandRepository
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
abstract class BrandDiModule {

    @Binds
    @Singleton
    abstract fun bindBrandRepository(repository: BrandRepositoryImpl): BrandRepository

    companion object {
        @Provides
        @Singleton
        fun provideBrandServiceApi(retrofit: Retrofit): BrandsApiService {
            return retrofit.create(BrandsApiService::class.java)
        }
    }
}