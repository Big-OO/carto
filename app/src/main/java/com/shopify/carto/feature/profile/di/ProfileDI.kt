package com.shopify.carto.feature.profile.di

import com.shopify.carto.feature.profile.data.remote.api.ProfileShopifyApi
import com.shopify.carto.feature.profile.data.remote.datasource.ProfileRemoteDataSource
import com.shopify.carto.feature.profile.data.remote.datasource.ProfileRemoteDataSourceImpl
import com.shopify.carto.feature.profile.domain.repository.ProfileRepository
import com.shopify.carto.feature.profile.domain.usecase.ObserveProfileUseCase
import com.shopify.carto.feature.profile.domain.usecase.RefreshProfileUseCase
import com.shopify.carto.feature.profile.domain.usecase.UpdateProfileUseCase
import com.shopify.carto.core.config.ShopifyConfig
import com.shopify.carto.feature.profile.data.remote.api.ProfileService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileDIModule {

    @Provides
    @Singleton
    fun provideProfileShopifyApi(retrofit: Retrofit): ProfileShopifyApi {
        return retrofit.create(ProfileShopifyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileService(retrofit: Retrofit): ProfileService {
        return retrofit.create(ProfileService::class.java)
    }


    @Provides
    @Singleton
    fun provideProfileRemoteDataSource(
        api: ProfileShopifyApi,
        config: ShopifyConfig
    ): ProfileRemoteDataSource {
        return ProfileRemoteDataSourceImpl(api, config)
    }

    @Provides
    @Singleton
    fun provideObserveProfileUseCase(repository: ProfileRepository): ObserveProfileUseCase {
        return ObserveProfileUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRefreshProfileUseCase(repository: ProfileRepository): RefreshProfileUseCase {
        return RefreshProfileUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateProfileUseCase(repository: ProfileRepository): UpdateProfileUseCase {
        return UpdateProfileUseCase(repository)
    }
}
