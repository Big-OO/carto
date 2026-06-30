package com.example.carto.feature.register.domain.usecases

import com.example.carto.feature.register.domain.model.RegisterRequest
import com.example.carto.feature.register.domain.model.RegisterResult
import com.example.carto.feature.register.domain.model.RegisteredUser
import com.example.carto.feature.register.domain.repository.RegisterRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val registerRepository: RegisterRepository,
) {
    suspend operator fun invoke(request: RegisterRequest): RegisterResult<RegisteredUser> {
        return registerRepository.register(request)
    }
}
