package com.shopify.carto.core.network.di

import com.shopify.carto.core.BuildConfig
import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.core.network.http.OkHttpClientFactory
import com.shopify.carto.core.network.interceptor.ShopifyAdminAuthInterceptor
import com.shopify.carto.core.network.interceptor.ShopifyStorefrontAuthInterceptor
import com.shopify.carto.core.network.qualifier.AdminAuthInterceptor
import com.shopify.carto.core.network.qualifier.AdminOkHttp
import com.shopify.carto.core.network.qualifier.NetworkLogger
import com.shopify.carto.core.network.qualifier.StorefrontAuthInterceptor
import com.shopify.carto.core.network.qualifier.StorefrontOkHttp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideShopifyConfig(): ShopifyConfig {
        return ShopifyConfig(
            hostname = BuildConfig.SHOPIFY_HOSTNAME,
            apiVersion = BuildConfig.SHOPIFY_API_VERSION,
            adminAccessToken = BuildConfig.SHOPIFY_ADMIN_ACCESS_TOKEN,
            storefrontAccessToken = BuildConfig.SHOPIFY_STOREFRONT_ACCESS_TOKEN,
        )
    }

    @NetworkLogger
    @Provides
    @Singleton
    fun provideNetworkLogger(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @AdminAuthInterceptor
    @Provides
    @Singleton
    fun provideAdminAuthInterceptor(config: ShopifyConfig): Interceptor {
        return ShopifyAdminAuthInterceptor(config)
    }

    @StorefrontAuthInterceptor
    @Provides
    @Singleton
    fun provideStorefrontAuthInterceptor(config: ShopifyConfig): Interceptor {
        return ShopifyStorefrontAuthInterceptor(config)
    }

    @AdminOkHttp
    @Provides
    @Singleton
    fun provideAdminOkHttpClient(
        @AdminAuthInterceptor authInterceptor: Interceptor,
        @NetworkLogger loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClientFactory.create(
            authInterceptor,
            loggingInterceptor,
        )
    }

    @StorefrontOkHttp
    @Provides
    @Singleton
    fun provideStorefrontOkHttpClient(
        @StorefrontAuthInterceptor authInterceptor: Interceptor,
        @NetworkLogger loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClientFactory.create(
            authInterceptor,
            loggingInterceptor,
        )
    }
}
