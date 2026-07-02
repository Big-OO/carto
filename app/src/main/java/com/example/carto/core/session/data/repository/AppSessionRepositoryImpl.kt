package com.example.carto.core.session.data.repository

import com.example.carto.core.session.data.local.AppSessionLocalDataSource
import com.example.carto.core.session.domain.model.AppSession
import com.example.carto.core.session.domain.repository.AppSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppSessionRepositoryImpl @Inject constructor(
    private val localDataSource: AppSessionLocalDataSource,
) : AppSessionRepository {
    override val session: Flow<AppSession> = localDataSource.session
    override suspend fun completeOnBoarding() {
        localDataSource.completeOnBoarding()
    }

    override suspend fun saveGuestSession() {
        localDataSource.saveGuestSession()
    }

    override suspend fun saveAuthenticatedSession(customerId: String?) {
        localDataSource.saveAuthenticatedSession(customerId)
    }

    override suspend fun clearSession() {
        localDataSource.clearSession()
    }
}
