package com.example.carto.feature.settings.data.repository

import com.example.carto.feature.settings.data.local.SettingsLocalDataSource
import com.example.carto.feature.settings.domain.model.AppLanguage
import com.example.carto.feature.settings.domain.model.AppTheme
import com.example.carto.feature.settings.domain.model.Currency
import com.example.carto.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override val theme: Flow<AppTheme> = settingsLocalDataSource.theme
    override val language: Flow<AppLanguage> = settingsLocalDataSource.language
    override val currency: Flow<Currency> = settingsLocalDataSource.currency

    override suspend fun setTheme(theme: AppTheme) {
        settingsLocalDataSource.setTheme(theme)
    }

    override suspend fun setLanguage(language: AppLanguage) {
        settingsLocalDataSource.setLanguage(language)
    }

    override suspend fun setCurrency(currency: Currency) {
        settingsLocalDataSource.setCurrency(currency)
    }
}
