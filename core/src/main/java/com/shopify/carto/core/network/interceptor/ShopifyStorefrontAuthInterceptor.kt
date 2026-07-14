package com.shopify.carto.core.network.interceptor

import com.shopify.carto.core.network.config.ShopifyConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ShopifyStorefrontAuthInterceptor @Inject constructor(
    private val shopifyConfig: ShopifyConfig
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Shopify-Storefront-Access-Token", shopifyConfig.storefrontAccessToken)
            .build()

        return chain.proceed(request)
    }
}