package com.shopify.carto.core.network.http

import okhttp3.Interceptor
import okhttp3.OkHttpClient

object OkHttpClientFactory {

    fun create(vararg interceptors: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                interceptors.forEach(::addInterceptor)
            }
            .build()
    }
}
