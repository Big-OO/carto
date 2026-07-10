package com.shopify.carto.core.network.di

import com.apollographql.apollo.ApolloClient
import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.core.network.graphql.ApolloClientFactory
import com.shopify.carto.core.network.qualifier.AdminApollo
import com.shopify.carto.core.network.qualifier.AdminOkHttp
import com.shopify.carto.core.network.qualifier.StorefrontApollo
import com.shopify.carto.core.network.qualifier.StorefrontOkHttp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApolloModule {

    @StorefrontApollo
    @Provides
    @Singleton
    fun provideStorefrontApolloClient(
        shopifyConfig: ShopifyConfig,
        @StorefrontOkHttp okHttpClient: OkHttpClient,
    ): ApolloClient {
        return ApolloClientFactory.create(
            serverUrl = shopifyConfig.storefrontGraphQlUrl,
            okHttpClient = okHttpClient,
        )
    }

    @AdminApollo
    @Provides
    @Singleton
    fun provideAdminApolloClient(
        shopifyConfig: ShopifyConfig,
        @AdminOkHttp okHttpClient: OkHttpClient,
    ): ApolloClient {
        return ApolloClientFactory.create(
            serverUrl = shopifyConfig.adminGraphQlUrl,
            okHttpClient = okHttpClient,
        )
    }
}
