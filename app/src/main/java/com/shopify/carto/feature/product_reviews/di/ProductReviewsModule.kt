package com.shopify.carto.feature.product_reviews.di

import com.google.gson.Gson
import com.shopify.carto.core.network.qualifier.AdminRetrofit
import com.shopify.carto.feature.product_reviews.data.data_source.ProductReviewsRemoteDataSource
import com.shopify.carto.feature.product_reviews.data.data_source.ProductReviewsRemoteDataSourceImpl
import com.shopify.carto.feature.product_reviews.data.repository.ProductReviewsRepositoryImpl
import com.shopify.carto.feature.product_reviews.data.service.ProductReviewsService
import com.shopify.carto.feature.product_reviews.domain.repository.ProductReviewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductReviewsModule {

    @Binds
    abstract fun bindProductReviewsRepository(
        implementation: ProductReviewsRepositoryImpl
    ): ProductReviewsRepository

    @Binds
    abstract fun bindProductReviewsRemoteDataSource(
        implementation: ProductReviewsRemoteDataSourceImpl
    ): ProductReviewsRemoteDataSource

    companion object {

        @Provides
        @Singleton
        fun provideProductReviewsService(@AdminRetrofit retrofit: Retrofit): ProductReviewsService {
            return retrofit.create(ProductReviewsService::class.java)
        }

        @Provides
        @Singleton
        fun provideGson(): Gson {
            return Gson()
        }
    }
}