package com.shopify.carto.feature.login.domain.repository

import com.shopify.carto.feature.login.domain.model.User

interface LoginRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginWithGoogle(idToken: String): Result<User>
}