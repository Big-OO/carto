package com.shopify.carto.feature.login.domain.usecase

import com.shopify.carto.feature.login.domain.model.User
import com.shopify.carto.feature.login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        return repository.login(email, password)
    }
}