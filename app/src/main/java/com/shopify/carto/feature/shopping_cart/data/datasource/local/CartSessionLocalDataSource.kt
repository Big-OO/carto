package com.shopify.carto.feature.shopping_cart.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CartSessionLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // Generate a dynamic key based on who is currently using the app
    private fun getCartKey(customerId: String?): Preferences.Key<String> {
        val keyName = if (customerId.isNullOrBlank()) "active_cart_id_guest" else "active_cart_id_$customerId"
        return stringPreferencesKey(keyName)
    }

    suspend fun getCartId(customerId: String?): String? {
        return dataStore.data.first()[getCartKey(customerId)]
    }

    suspend fun saveCartId(cartId: String, customerId: String?) {
        dataStore.edit { preferences -> preferences[getCartKey(customerId)] = cartId }
    }

    suspend fun clearCartId(customerId: String?) {
        dataStore.edit { preferences -> preferences.remove(getCartKey(customerId)) }
    }
}