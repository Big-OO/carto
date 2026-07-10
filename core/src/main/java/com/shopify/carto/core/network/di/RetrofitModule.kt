package com.shopify.carto.core.network.di

import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.core.network.qualifier.AdminOkHttp
import com.shopify.carto.core.network.qualifier.AdminRetrofit
import com.shopify.carto.core.network.rest.RetrofitFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @AdminRetrofit
    @Provides
    @Singleton
    fun provideAdminRetrofit(
        shopifyConfig: ShopifyConfig,
        @AdminOkHttp okHttpClient: OkHttpClient
    ): Retrofit {
        return RetrofitFactory.create(
            baseUrl = shopifyConfig.adminRestBaseUrl,
            client = okHttpClient
        )
    }
}