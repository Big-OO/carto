package com.example.carto.feature.register.data.shopify

import com.example.carto.core.config.ShopifyConfig
import com.example.carto.feature.register.data.result.RegisterDataResult
import com.example.carto.feature.register.data.shopify.model.CreateShopifyCustomerRequest
import com.example.carto.feature.register.data.shopify.model.ShopifyCustomerBody
import com.example.carto.feature.register.domain.model.RegisterFailure
import com.example.carto.feature.register.domain.model.RegisterFailureType
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class ShopifyCustomerRemoteDataSource @Inject constructor(
    private val api: RegisterShopifyApi,
    private val config: ShopifyConfig,
) {
    suspend fun getOrCreateCustomerId(fullName: String, email: String): RegisterDataResult<Long> {
        return when (val searchResult = searchExistingCustomerId(email)) {
            is RegisterDataResult.Failure -> searchResult
            is RegisterDataResult.Success -> {
                val existingCustomerId = searchResult.data
                if (existingCustomerId != null) {
                    RegisterDataResult.Success(existingCustomerId)
                } else {
                    createCustomer(fullName = fullName, email = email)
                }
            }
        }
    }

    private suspend fun searchExistingCustomerId(email: String): RegisterDataResult<Long?> {
        return try {
            val response = api.searchCustomerByEmail(
                version = config.apiVersion,
                query = "email:${email.trim()}",
            )

            if (!response.isSuccessful) {
                return RegisterDataResult.Failure(response.toShopifyFailure("search customer by email"))
            }

            val customerId = response.body()
                ?.customers
                ?.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
                ?.id

            RegisterDataResult.Success(customerId)
        } catch (exception: Exception) {
            RegisterDataResult.Failure(exception.toShopifyFailure("search customer by email"))
        }
    }

    private suspend fun createCustomer(fullName: String, email: String): RegisterDataResult<Long> {
        return try {
            val response = api.createCustomer(
                version = config.apiVersion,
                body = CreateShopifyCustomerRequest(
                    customer = ShopifyCustomerBody(
                        firstName = extractFirstName(fullName),
                        lastName = extractLastName(fullName),
                        email = email.trim(),
                        verifiedEmail = false,
                        sendEmailWelcome = false,
                        tags = "mobile-app,firebase-auth",
                        note = "Created from Carto Android registration flow",
                    )
                )
            )

            if (!response.isSuccessful) {
                return RegisterDataResult.Failure(response.toShopifyFailure("create customer"))
            }

            val customerId = response.body()?.customer?.id
                ?: return RegisterDataResult.Failure(
                    RegisterFailure(
                        type = RegisterFailureType.ShopifySyncFailed,
                        message = "Shopify create customer succeeded but response body/customer/id was null.",
                    )
                )

            RegisterDataResult.Success(customerId)
        } catch (exception: Exception) {
            RegisterDataResult.Failure(exception.toShopifyFailure("create customer"))
        }
    }

    private fun Response<out Any>.toShopifyFailure(operation: String): RegisterFailure {
        val errorBody = runCatching { errorBody()?.string() }.getOrNull().orEmpty()
        return RegisterFailure(
            type = RegisterFailureType.ShopifySyncFailed,
            message = "Shopify $operation failed. statusCode=${code()}, errorBody=${errorBody.ifBlank { "No error body." }}",
        )
    }

    private fun Exception.toShopifyFailure(operation: String): RegisterFailure {
        val type = if (this is IOException) {
            RegisterFailureType.Network
        } else {
            RegisterFailureType.ShopifySyncFailed
        }

        return RegisterFailure(
            type = type,
            message = "Shopify $operation failed with ${this::class.java.name}: ${message.orEmpty().ifBlank { "No message provided." }}",
        )
    }

    private fun extractFirstName(fullName: String): String {
        return fullName.trim().split(Regex("\\s+")).firstOrNull().orEmpty()
    }

    private fun extractLastName(fullName: String): String {
        return fullName.trim().split(Regex("\\s+")).drop(1).joinToString(" ")
    }
}
