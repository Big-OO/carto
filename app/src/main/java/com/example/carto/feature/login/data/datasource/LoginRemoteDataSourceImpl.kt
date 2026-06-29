package com.example.carto.feature.login.data.datasource

import com.example.carto.feature.login.data.dto.UserDto
import com.example.carto.feature.login.data.mapper.toDto
import com.example.carto.feature.login.domain.datasource.LoginRemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRemoteDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : LoginRemoteDataSource {

    override suspend fun login(
        email: String,
        password: String
    ): UserDto {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()

        val user = requireNotNull(result.user)

        return user.toDto()
    }
}