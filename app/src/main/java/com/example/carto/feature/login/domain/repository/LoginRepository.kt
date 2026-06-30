package com.example.carto.feature.login.domain.repository

import com.example.carto.feature.login.domain.model.User

interface LoginRepository {
    suspend fun login(email: String, password: String): Result<User>
}