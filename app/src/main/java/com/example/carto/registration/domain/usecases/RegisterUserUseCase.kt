package com.example.carto.registration.domain.usecases

import com.example.carto.registration.domain.model.RegisterRequest
import com.example.carto.registration.domain.model.RegisterResult
import com.example.carto.registration.domain.model.RegisteredUser
import com.example.carto.registration.domain.repository.RegisterRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val registerRepository: RegisterRepository,
) {
    suspend operator fun invoke(request: RegisterRequest): RegisterResult<RegisteredUser> {
        return registerRepository.register(request)
    }
}
