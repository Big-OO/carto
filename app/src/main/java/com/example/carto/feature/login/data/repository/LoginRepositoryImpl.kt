package com.example.carto.feature.login.data.repository

import android.util.Log
import com.example.carto.feature.login.data.mapper.toDomain
import com.example.carto.feature.login.domain.datasource.LoginRemoteDataSource
import com.example.carto.feature.login.domain.model.User
import com.example.carto.feature.login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val remoteDataSource: LoginRemoteDataSource
) : LoginRepository {

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return try {
            val user = remoteDataSource
                .login(email, password)
                .toDomain()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
