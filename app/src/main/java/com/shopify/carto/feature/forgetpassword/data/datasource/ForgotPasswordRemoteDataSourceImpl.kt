package com.shopify.carto.feature.forgetpassword.data.datasource

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ForgotPasswordRemoteDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ForgotPasswordRemoteDataSource {
    override suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }
}
