package com.shopify.carto.feature.orderhistory.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HiddenOrdersLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    fun observeHiddenOrderIds(): Flow<Set<String>> {
        return dataStore.data
            .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { preferences -> preferences[HIDDEN_ORDER_IDS].orEmpty() }
    }

    suspend fun hideOrder(orderId: String) {
        dataStore.edit { preferences ->
            val currentIds = preferences[HIDDEN_ORDER_IDS].orEmpty()
            preferences[HIDDEN_ORDER_IDS] = currentIds + orderId
        }
    }

    private companion object {
        val HIDDEN_ORDER_IDS = stringSetPreferencesKey("hidden_order_ids")
    }
}
