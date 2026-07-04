package com.shopify.carto.core.session.data.repository

import com.shopify.carto.core.session.data.local.AppSessionLocalDataSource
import com.shopify.carto.core.session.domain.model.AppSession
import com.shopify.carto.core.session.domain.repository.AppSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth


class AppSessionRepositoryImpl @Inject constructor(
    private val localDataSource: AppSessionLocalDataSource,
    private val firebaseAuth: FirebaseAuth,
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
        firebaseAuth.signOut()
        localDataSource.clearSession()
    }
}