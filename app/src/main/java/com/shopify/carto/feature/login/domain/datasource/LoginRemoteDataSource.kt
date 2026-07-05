package com.shopify.carto.feature.login.domain.datasource

import com.shopify.carto.feature.login.data.dto.UserDto

interface LoginRemoteDataSource {
    suspend fun login(
        email: String,
        password: String
    ): UserDto
}
