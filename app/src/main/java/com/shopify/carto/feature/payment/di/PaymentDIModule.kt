package com.shopify.carto.feature.payment.di

import com.shopify.carto.core.network.http.OkHttpClientFactory
import com.shopify.carto.core.network.qualifier.NetworkLogger
import com.shopify.carto.core.network.qualifier.PaymobRetrofit
import com.shopify.carto.core.network.qualifier.PaymobFlashRetrofit
import com.shopify.carto.core.network.rest.RetrofitFactory
import com.shopify.carto.feature.payment.data.remote.PaymentApiService
import com.shopify.carto.feature.payment.data.remote.PaymobFlashApiService
import com.shopify.carto.feature.payment.data.remote.PaymentRemoteDataSource
import com.shopify.carto.feature.payment.data.remote.PaymentRemoteDataSourceImpl
import com.shopify.carto.feature.payment.data.repository.PaymentRepositoryImpl
import com.shopify.carto.feature.payment.domain.repository.PaymentRepository
import com.shopify.carto.feature.payment.domain.usecase.ValidateCheckoutUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton
import com.shopify.carto.BuildConfig
@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        repository: PaymentRepositoryImpl,
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRemoteDataSource(
        dataSource: PaymentRemoteDataSourceImpl,
    ): PaymentRemoteDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object PaymentNetworkModule {

    @PaymobRetrofit
    @Provides
    @Singleton
    fun providePaymobOkHttpClient(
        @NetworkLogger loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClientFactory.create(loggingInterceptor)
    }

    @PaymobRetrofit
    @Provides
    @Singleton
    fun providePaymobRetrofit(
        @PaymobRetrofit okHttpClient: OkHttpClient,
    ): Retrofit {
        return RetrofitFactory.create(
            baseUrl = BuildConfig.SUPABASE_BASE_URL,
            client = okHttpClient,
        )
    }

    @PaymobFlashRetrofit
    @Provides
    @Singleton
    fun providePaymobFlashRetrofit(
        @PaymobRetrofit okHttpClient: OkHttpClient,
    ): Retrofit {
        return RetrofitFactory.create(
            baseUrl = BuildConfig.PAYMOB_FLASH_BASE_URL,
            client = okHttpClient,
        )
    }

    @Provides
    @Singleton
    fun providePaymentApiService(@PaymobRetrofit retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymobFlashApiService(@PaymobFlashRetrofit retrofit: Retrofit): PaymobFlashApiService {
        return retrofit.create(PaymobFlashApiService::class.java)
    }

    @Provides
    fun provideValidateCheckoutUseCase(): ValidateCheckoutUseCase {
        return ValidateCheckoutUseCase()
    }
}
