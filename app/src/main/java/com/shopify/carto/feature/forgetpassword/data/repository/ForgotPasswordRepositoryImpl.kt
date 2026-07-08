package com.shopify.carto.feature.forgetpassword.data.repository

import com.shopify.carto.feature.forgetpassword.data.datasource.ForgotPasswordRemoteDataSource
import com.shopify.carto.feature.forgetpassword.domain.repository.ForgotPasswordRepository
import javax.inject.Inject

class ForgotPasswordRepositoryImpl @Inject constructor(
    private val remoteDataSource: ForgotPasswordRemoteDataSource
) : ForgotPasswordRepository {
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            remoteDataSource.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
