package com.shopify.carto.core.session.domain.repository

import com.shopify.carto.core.session.domain.model.AppSession
import kotlinx.coroutines.flow.Flow

interface AppSessionRepository {
    val session: Flow<AppSession>

    suspend fun completeOnBoarding()

    suspend fun saveGuestSession()

    suspend fun saveAuthenticatedSession(customerId: String?)

    suspend fun clearSession()
}
