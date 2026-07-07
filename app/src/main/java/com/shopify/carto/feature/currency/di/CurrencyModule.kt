package com.shopify.carto.feature.currency.di

import com.google.gson.Gson
import com.shopify.carto.feature.currency.data.local.CurrencyPreferences
import com.shopify.carto.feature.currency.data.remote.CurrencyApiService
import com.shopify.carto.feature.currency.data.repository.CurrencyRepositoryImpl
import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CurrencyModule {

    @Provides
    @Singleton
    fun provideCurrencyApiService(@com.shopify.carto.core.network.qualifier.AdminRetrofit retrofit: Retrofit): CurrencyApiService {
        // Here we create a new retrofit instance just for the exchange rate API, since it's a different base url.
        val exchangeRetrofit = retrofit.newBuilder()
            .baseUrl("https://api.exchangerate-api.com/v4/")
            .build()
        return exchangeRetrofit.create(CurrencyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(
        apiService: CurrencyApiService,
        preferences: CurrencyPreferences,
        gson: Gson
    ): CurrencyRepository {
        return CurrencyRepositoryImpl(apiService, preferences, gson)
    }
}
