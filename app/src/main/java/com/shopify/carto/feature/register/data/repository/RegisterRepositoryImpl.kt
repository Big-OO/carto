package com.shopify.carto.feature.register.data.repository

import com.shopify.carto.feature.register.data.firebase.FirebaseRegisterDataSource
import com.shopify.carto.feature.register.data.result.RegisterDataResult
import com.shopify.carto.feature.register.data.shopify.ShopifyCustomerRemoteDataSource
import com.shopify.carto.feature.register.domain.model.RegisterFailure
import com.shopify.carto.feature.register.domain.model.RegisterFailureType
import com.shopify.carto.feature.register.domain.model.RegisterRequest
import com.shopify.carto.feature.register.domain.model.RegisterResult
import com.shopify.carto.feature.register.domain.model.RegisteredUser
import com.shopify.carto.feature.register.domain.repository.RegisterRepository
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
