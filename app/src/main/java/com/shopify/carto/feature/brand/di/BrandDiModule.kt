package com.shopify.carto.feature.brand.di

import com.shopify.carto.core.network.qualifier.AdminRetrofit
import com.shopify.carto.feature.brand.data.remote.api.BrandsApiService
import com.shopify.carto.feature.brand.data.remote.repository.BrandRepositoryImpl
import com.shopify.carto.feature.brand.data.remote.network.BrandNetworkDataSource
import com.shopify.carto.feature.brand.data.remote.network.RetrofitBrandNetworkDataSource
import com.shopify.carto.feature.brand.domain.repository.BrandRepository
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

    @Binds
    @Singleton
    abstract fun bindBrandNetworkDataSource(dataSource: RetrofitBrandNetworkDataSource): BrandNetworkDataSource

    companion object {
        @Provides
        @Singleton
        fun provideBrandServiceApi(@AdminRetrofit retrofit: Retrofit): BrandsApiService {
            return retrofit.create(BrandsApiService::class.java)
        }
    }
}