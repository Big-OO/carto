package com.shopify.carto.feature.ai_integration.di

import android.content.Context
import com.shopify.carto.feature.ai_integration.ai.AIShoppingAgent
import com.shopify.carto.feature.ai_integration.ai.ShoppingAppFunctionRunner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIDIModule {

    @Provides
    @Singleton
    fun provideShoppingAppFunctionRunner(
        @ApplicationContext context: Context
    ): ShoppingAppFunctionRunner {
        return ShoppingAppFunctionRunner(context)
    }

    @Provides
    @Singleton
    fun provideAIShoppingAgent(
        appFunctionRunner: ShoppingAppFunctionRunner
    ): AIShoppingAgent {
        return AIShoppingAgent(appFunctionRunner)
    }
}
