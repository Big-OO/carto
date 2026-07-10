package com.shopify.carto.core.notification.di

import com.shopify.carto.core.notification.data.repositroy.NotificationRepositoryImpl
import com.shopify.carto.core.notification.domain.repositroy.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl,
    ): NotificationRepository
}
