package com.shopify.carto.feature.addresses.di

import com.shopify.carto.feature.addresses.data.remote.service.AddressesShopifyApi
import com.shopify.carto.feature.addresses.data.repository.AddressesRepositoryImpl
import com.shopify.carto.feature.addresses.domain.repository.AddressesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AddressesRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAddressesRepository(
        repository: AddressesRepositoryImpl,
    ): AddressesRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AddressesNetworkModule {
    @Provides
    @Singleton
    fun provideAddressesShopifyApi(retrofit: Retrofit): AddressesShopifyApi {
        return retrofit.create(AddressesShopifyApi::class.java)
    }
}
