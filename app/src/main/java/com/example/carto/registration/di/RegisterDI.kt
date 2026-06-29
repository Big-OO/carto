package com.example.carto.registration.di

import com.example.carto.BuildConfig
import com.example.carto.registration.data.firebase.FirebaseRegisterDataSource
import com.example.carto.registration.data.repository.RegisterRepositoryImpl
import com.example.carto.registration.data.shopify.RegisterShopifyApi
import com.example.carto.registration.data.shopify.ShopifyCustomerRemoteDataSource
import com.example.carto.registration.data.shopify.ShopifyRegisterConfig
import com.example.carto.registration.domain.repository.RegisterRepository
import com.example.carto.registration.domain.usecases.RegisterUserUseCase
import com.example.carto.registration.domain.usecases.ValidateEmailUseCase
import com.example.carto.registration.domain.usecases.ValidateFullNameUseCase
import com.example.carto.registration.domain.usecases.ValidatePasswordUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RegisterDIModule {
    @Provides
    @Singleton
    fun provideValidateEmailUseCase(): ValidateEmailUseCase {
        return ValidateEmailUseCase()
    }

    @Provides
    @Singleton
    fun provideValidateFullNameUseCase(): ValidateFullNameUseCase {
        return ValidateFullNameUseCase()
    }

    @Provides
    @Singleton
    fun provideValidatePasswordUseCase(): ValidatePasswordUseCase {
        return ValidatePasswordUseCase()
    }

    @Provides
    @Singleton
    fun provideRegisterUserUseCase(registerRepository: RegisterRepository): RegisterUserUseCase {
        return RegisterUserUseCase(registerRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterRepository(
        firebaseDataSource: FirebaseRegisterDataSource,
        shopifyCustomerRemoteDataSource: ShopifyCustomerRemoteDataSource,
    ): RegisterRepository {
        return RegisterRepositoryImpl(
            firebaseDataSource = firebaseDataSource,
            shopifyRemoteDataSource = shopifyCustomerRemoteDataSource,
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideShopifyRegisterConfig(): ShopifyRegisterConfig {
        return ShopifyRegisterConfig(
            hostname = BuildConfig.SHOPIFY_HOSTNAME,
            apiVersion = BuildConfig.SHOPIFY_API_VERSION,
            adminAccessToken = BuildConfig.SHOPIFY_ADMIN_ACCESS_TOKEN,
        )
    }

    @Provides
    @Singleton
    fun provideRegisterShopifyApi(config: ShopifyRegisterConfig): RegisterShopifyApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Shopify-Access-Token", config.adminAccessToken)
                    .build()

                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://${config.hostname}/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RegisterShopifyApi::class.java)
    }
}
