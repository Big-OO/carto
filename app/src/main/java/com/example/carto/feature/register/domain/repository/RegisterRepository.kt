package com.example.carto.feature.register.domain.repository

import com.example.carto.feature.register.domain.model.RegisterRequest
import com.example.carto.feature.register.domain.model.RegisterResult
import com.example.carto.feature.register.domain.model.RegisteredUser

interface RegisterRepository {
    suspend fun register(request: RegisterRequest): RegisterResult<RegisteredUser>
}
