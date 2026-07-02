package com.example.carto.core.session.data.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.carto.core.session.domain.model.AppSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AppSessionLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val session: Flow<AppSession> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppSession(
                isLoggedIn = preferences[IS_LOGGED_IN] ?: false,
                isGuest = preferences[IS_GUEST] ?: false,
                customerId = preferences[CUSTOMER_ID],
            )
        }

    suspend fun saveGuestSession() {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[IS_GUEST] = true
            preferences.remove(CUSTOMER_ID)
        }
    }

    suspend fun saveAuthenticatedSession(customerId: String?) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true
            preferences[IS_GUEST] = false
            if (customerId.isNullOrBlank()) {
                preferences.remove(CUSTOMER_ID)
            } else {
                preferences[CUSTOMER_ID] = customerId
            }
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_GUEST = booleanPreferencesKey("is_guest")
        val CUSTOMER_ID = stringPreferencesKey("customer_id")
    }
}
