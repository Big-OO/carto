package com.example.carto.feature.brand.di

import com.example.carto.feature.brand.data.remote.api.BrandsApiService
import com.example.carto.feature.brand.data.remote.repository.BrandRepositoryImpl
import com.example.carto.feature.brand.domain.repository.BrandRepository
import com.example.carto.feature.search.data.remote.SearchShopifyApi
import com.example.carto.feature.search.data.repository.SearchRepositoryImpl
import com.example.carto.feature.search.domain.repository.SearchRepository
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