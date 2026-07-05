package com.shopify.carto.feature.register.domain.repository

import com.shopify.carto.feature.register.domain.model.RegisterRequest
import com.shopify.carto.feature.register.domain.model.RegisterResult
import com.shopify.carto.feature.register.domain.model.RegisteredUser

interface RegisterRepository {
    suspend fun register(request: RegisterRequest): RegisterResult<RegisteredUser>
}
