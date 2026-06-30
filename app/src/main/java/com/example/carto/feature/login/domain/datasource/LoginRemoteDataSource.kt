package com.example.carto.feature.login.domain.datasource

import com.example.carto.feature.login.data.dto.UserDto

interface LoginRemoteDataSource {
    suspend fun login(
        email: String,
        password: String
    ): UserDto
}
