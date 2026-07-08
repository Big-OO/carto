package com.shopify.carto.feature.shopping_cart.di

import com.google.firebase.firestore.FirebaseFirestore
import com.shopify.carto.feature.shopping_cart.data.datasource.remote.CartRemoteDataSource
import com.shopify.carto.feature.shopping_cart.data.datasource.remote.CartRemoteDataSourceImpl
import com.shopify.carto.feature.shopping_cart.data.datasource.remote.CartSessionRemoteDataSource
import com.shopify.carto.feature.shopping_cart.data.datasource.remote.CartSessionRemoteDataSourceImpl
import com.shopify.carto.feature.shopping_cart.data.repository.CartRepositoryImpl
import com.shopify.carto.feature.shopping_cart.domain.repository.CartRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {

    @Binds
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    abstract fun bindCartRemoteDataSource(impl: CartRemoteDataSourceImpl): CartRemoteDataSource

    @Binds
    abstract fun bindCartSessionRemoteDataSource(impl: CartSessionRemoteDataSourceImpl): CartSessionRemoteDataSource


}