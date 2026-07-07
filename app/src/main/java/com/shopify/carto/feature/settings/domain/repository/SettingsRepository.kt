package com.shopify.carto.feature.settings.domain.repository

import com.shopify.carto.feature.settings.domain.model.AppLanguage
import com.shopify.carto.feature.settings.domain.model.AppTheme
import com.shopify.carto.feature.currency.domain.model.Currency
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<AppTheme>
    val language: Flow<AppLanguage>
    val currency: Flow<Currency>

    suspend fun setTheme(theme: AppTheme)
    suspend fun setLanguage(language: AppLanguage)
    suspend fun setCurrency(currency: Currency)
}
