package com.shopify.carto.feature.forgetpassword.data.datasource

interface ForgotPasswordRemoteDataSource {
    suspend fun sendPasswordResetEmail(email: String)
}
