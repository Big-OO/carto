package com.shopify.carto.core.session.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.shopify.carto.core.session.domain.model.AppSession
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
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppSession(
                isOnboardingSeen = preferences[IS_ONBOARDING_COMPLETED] ?: false,
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

    suspend fun completeOnBoarding() {
        dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN)
            preferences.remove(IS_GUEST)
            preferences.remove(CUSTOMER_ID)
        }
    }

    private companion object {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_GUEST = booleanPreferencesKey("is_guest")
        val CUSTOMER_ID = stringPreferencesKey("customer_id")
    }
}