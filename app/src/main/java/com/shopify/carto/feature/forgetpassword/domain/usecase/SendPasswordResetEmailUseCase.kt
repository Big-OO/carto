package com.shopify.carto.feature.forgetpassword.domain.usecase

import com.shopify.carto.feature.forgetpassword.domain.repository.ForgotPasswordRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val repository: ForgotPasswordRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        return repository.sendPasswordResetEmail(email)
    }
}
