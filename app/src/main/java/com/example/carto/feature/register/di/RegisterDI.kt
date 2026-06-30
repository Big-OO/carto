package com.example.carto.feature.register.di

import com.example.carto.feature.register.data.firebase.FirebaseRegisterDataSource
import com.example.carto.feature.register.data.repository.RegisterRepositoryImpl
import com.example.carto.feature.register.data.shopify.RegisterShopifyApi
import com.example.carto.feature.register.data.shopify.ShopifyCustomerRemoteDataSource
import com.example.carto.feature.register.domain.repository.RegisterRepository
import com.example.carto.feature.register.domain.usecases.RegisterUserUseCase
import com.example.carto.feature.register.domain.usecases.ValidateEmailUseCase
import com.example.carto.feature.register.domain.usecases.ValidateFullNameUseCase
import com.example.carto.feature.register.domain.usecases.ValidatePasswordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
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
    fun provideRegisterShopifyApi(retrofit: Retrofit): RegisterShopifyApi {
        return retrofit.create(RegisterShopifyApi::class.java)
    }
}
