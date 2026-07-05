package com.shopify.carto.feature.shopping_cart.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val CART_ID_KEY = stringPreferencesKey("active_cart_id")

class CartSessionLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun getCartId(): String? {
        return dataStore.data.first()[CART_ID_KEY]
    }

    suspend fun saveCartId(cartId: String) {
        dataStore.edit { preferences -> preferences[CART_ID_KEY] = cartId }
    }

    suspend fun clearCartId() {
        dataStore.edit { preferences -> preferences.remove(CART_ID_KEY) }
    }
}