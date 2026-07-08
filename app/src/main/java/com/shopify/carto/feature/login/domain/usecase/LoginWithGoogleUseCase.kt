package com.shopify.carto.feature.login.domain.usecase

import com.shopify.carto.feature.login.domain.model.User
import com.shopify.carto.feature.login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        return repository.loginWithGoogle(idToken)
    }
}
