package com.example.carto.feature.settings.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.carto.feature.settings.domain.model.AppLanguage
import com.example.carto.feature.settings.domain.model.AppTheme
import com.example.carto.feature.settings.domain.model.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val theme: Flow<AppTheme> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themeName = preferences[THEME_KEY] ?: AppTheme.LIGHT.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                AppTheme.LIGHT
            }
        }

    val language: Flow<AppLanguage> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val langName = preferences[LANGUAGE_KEY] ?: AppLanguage.ENGLISH.name
            try {
                AppLanguage.valueOf(langName)
            } catch (e: IllegalArgumentException) {
                AppLanguage.ENGLISH
            }
        }

    val currency: Flow<Currency> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val currencyName = preferences[CURRENCY_KEY] ?: Currency.USD.name
            try {
                Currency.valueOf(currencyName)
            } catch (e: IllegalArgumentException) {
                Currency.USD
            }
        }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.name
        }
    }

    suspend fun setCurrency(currency: Currency) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency.name
        }
    }

    private companion object {
        val THEME_KEY = stringPreferencesKey("app_theme")
        val LANGUAGE_KEY = stringPreferencesKey("app_language")
        val CURRENCY_KEY = stringPreferencesKey("app_currency")
    }
}
