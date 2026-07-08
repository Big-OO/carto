package com.shopify.carto.feature.currency.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.currencyDataStore: DataStore<Preferences> by preferencesDataStore(name = "currency_prefs")

@Singleton
class CurrencyPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.currencyDataStore

    companion object {
        val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
        val RATES_JSON = stringPreferencesKey("rates_json")
        val LAST_UPDATED = longPreferencesKey("last_updated")
    }

    val selectedCurrencyFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[SELECTED_CURRENCY] ?: "USD"
    }

    val ratesJsonFlow: Flow<String?> = dataStore.data.map { prefs ->
        prefs[RATES_JSON]
    }
    
    val lastUpdatedFlow: Flow<Long> = dataStore.data.map { prefs ->
        prefs[LAST_UPDATED] ?: 0L
    }

    suspend fun saveSelectedCurrency(currency: String) {
        dataStore.edit { prefs ->
            prefs[SELECTED_CURRENCY] = currency
        }
    }

    suspend fun saveRates(ratesJson: String, lastUpdated: Long) {
        dataStore.edit { prefs ->
            prefs[RATES_JSON] = ratesJson
            prefs[LAST_UPDATED] = lastUpdated
        }
    }
}
