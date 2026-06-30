package com.example.carto.registration.domain.repository

import com.example.carto.registration.domain.model.RegisterRequest
import com.example.carto.registration.domain.model.RegisterResult
import com.example.carto.registration.domain.model.RegisteredUser

interface RegisterRepository {
    suspend fun register(request: RegisterRequest): RegisterResult<RegisteredUser>
}
