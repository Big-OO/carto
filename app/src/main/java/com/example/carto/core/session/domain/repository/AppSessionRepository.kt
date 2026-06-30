package com.example.carto.core.session.domain.repository

import com.example.carto.core.session.domain.model.AppSession
import kotlinx.coroutines.flow.Flow

interface AppSessionRepository {
    val session: Flow<AppSession>

    suspend fun saveGuestSession()

    suspend fun saveAuthenticatedSession(customerId: String?)

    suspend fun clearSession()
}
