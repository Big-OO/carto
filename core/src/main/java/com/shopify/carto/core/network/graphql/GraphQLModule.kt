package com.shopify.carto.core.network.graphql.di

import com.apollographql.apollo.ApolloClient
import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.core.network.graphql.ApolloClientFactory
import com.shopify.carto.core.network.interceptor.ShopifyStorefrontAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GraphQLModule {

    @Provides
    @Singleton
    fun provideStorefrontOkHttpClient(
        authInterceptor: ShopifyStorefrontAuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApolloClient(
        okHttpClient: OkHttpClient,
        shopifyConfig: ShopifyConfig
    ): ApolloClient {
        return ApolloClientFactory.create(
            serverUrl = shopifyConfig.storefrontGraphQlUrl,
            okHttpClient = okHttpClient
        )
    }
}