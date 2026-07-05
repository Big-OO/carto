package com.shopify.carto.core.network.qualifier

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdminRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StorefrontApollo

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdminOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StorefrontOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkLogger

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdminAuthInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StorefrontAuthInterceptor
