package com.example.carto.registration.data.repository

import com.example.carto.registration.data.firebase.FirebaseRegisterDataSource
import com.example.carto.registration.data.result.RegisterDataResult
import com.example.carto.registration.data.shopify.ShopifyCustomerRemoteDataSource
import com.example.carto.registration.domain.model.RegisterFailure
import com.example.carto.registration.domain.model.RegisterFailureType
import com.example.carto.registration.domain.model.RegisterRequest
import com.example.carto.registration.domain.model.RegisterResult
import com.example.carto.registration.domain.model.RegisteredUser
import com.example.carto.registration.domain.repository.RegisterRepository
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseRegisterDataSource,
    private val shopifyRemoteDataSource: ShopifyCustomerRemoteDataSource,
) : RegisterRepository {
    override suspend fun register(request: RegisterRequest): RegisterResult<RegisteredUser> {

        val firebaseUid = when (
            val firebaseResult = firebaseDataSource.createUser(
                fullName = request.fullName,
                email = request.email,
                password = request.password,
            )
        ) {
            is RegisterDataResult.Success -> firebaseResult.data
            is RegisterDataResult.Failure -> return firebaseResult.failure.toDomainResult()
        }

        val shopifyCustomerId = when (
            val shopifyResult = shopifyRemoteDataSource.getOrCreateCustomerId(
                fullName = request.fullName,
                email = request.email,
            )
        ) {
            is RegisterDataResult.Success -> shopifyResult.data
            is RegisterDataResult.Failure -> {
                firebaseDataSource.deleteCurrentUserSilently()
                return shopifyResult.failure.toDomainResult()
            }
        }

        return when (
            val saveProfileResult = firebaseDataSource.saveUserProfile(
                firebaseUid = firebaseUid,
                fullName = request.fullName,
                email = request.email,
                shopifyCustomerId = shopifyCustomerId,
            )
        ) {
            is RegisterDataResult.Success -> RegisterResult.Success(
                RegisteredUser(
                    firebaseUid = firebaseUid,
                    shopifyCustomerId = shopifyCustomerId,
                )
            )

            is RegisterDataResult.Failure -> {
                firebaseDataSource.deleteCurrentUserSilently()
                saveProfileResult.failure
                    .withFallbackType(RegisterFailureType.FirebaseSyncFailed)
                    .toDomainResult()
            }
        }
    }

    private fun RegisterFailure.toDomainResult(): RegisterResult.Failure {
        return RegisterResult.Failure(this)
    }

    private fun RegisterFailure.withFallbackType(type: RegisterFailureType): RegisterFailure {
        return if (this.type == RegisterFailureType.Unknown) {
            copy(type = type)
        } else {
            this
        }
    }
}
