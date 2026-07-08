package com.shopify.carto.feature.forgetpassword.domain.repository

interface ForgotPasswordRepository {
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
}
