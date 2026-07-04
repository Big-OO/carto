package com.shopify.carto.feature.addresses.data.remote

import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.feature.addresses.data.remote.dto.AddressDto
import com.shopify.carto.feature.addresses.data.remote.dto.CreateAddressBodyDto
import com.shopify.carto.feature.addresses.data.remote.dto.CreateAddressDto
import com.shopify.carto.feature.addresses.data.remote.error.AddressCreationError
import com.shopify.carto.feature.addresses.data.remote.error.DeleteAddressError
import com.shopify.carto.feature.addresses.data.remote.error.GetAddressesError
import com.shopify.carto.feature.addresses.data.remote.error.SetDefaultAddressError
import com.shopify.carto.feature.addresses.data.remote.network.AddressesNetworkDataSource
import com.shopify.carto.feature.addresses.data.result.AddressDataResult
import com.shopify.carto.feature.addresses.domain.model.AddressForm
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import javax.inject.Inject

class AddressesRemoteDataSource @Inject constructor(
    private val networkDataSource: AddressesNetworkDataSource,
    private val config: ShopifyConfig,
) {
    suspend fun getAddresses(customerId: Long): AddressDataResult<List<AddressDto>, GetAddressesError> {
        return try {
            val response = networkDataSource.getAddresses(config.apiVersion, customerId)
            if (response.isSuccessful) {
                AddressDataResult.Success(response.body()?.addresses.orEmpty())
            } else {
                AddressDataResult.Failure(
                    code = response.code(),
                    error = response.code().toGetAddressesError(),
                    message = response.errorBody()?.string(),
                )
            }
        } catch (throwable: IOException) {
            AddressDataResult.Failure(error = GetAddressesError.Network, message = throwable.message)
        } catch (throwable: Throwable) {
            AddressDataResult.Failure(error = GetAddressesError.Unknown, message = throwable.message)
        }
    }

    suspend fun createAddress(customerId: Long, form: AddressForm): AddressDataResult<AddressDto, AddressCreationError> {
        return try {
            val body = CreateAddressBodyDto(
                address = CreateAddressDto(
                    address1 = form.address1,
                    city = form.city,
                    province = form.province,
                    country = form.country,
                    zip = form.zip,
                    firstName = form.firstName,
                    lastName = form.lastName,
                )
            )
            val response = networkDataSource.createAddress(config.apiVersion, customerId, body)
            val address = response.body()?.address
            if (response.isSuccessful && address != null) {
                AddressDataResult.Success(address)
            } else {
                val errorMessage = response.errorBody()?.string()
                AddressDataResult.Failure(
                    code = response.code(),
                    error = responseErrorToAddressError(response.code(), errorMessage),
                    message = errorMessage,
                )
            }
        } catch (throwable: IOException) {
            AddressDataResult.Failure(error = AddressCreationError.Network, message = throwable.message)
        } catch (throwable: Throwable) {
            AddressDataResult.Failure(error = AddressCreationError.Unknown, message = throwable.message)
        }
    }

    suspend fun setDefaultAddress(customerId: Long, addressId: Long): AddressDataResult<Unit, SetDefaultAddressError> {
        return try {
            val response = networkDataSource.setDefaultAddress(config.apiVersion, customerId, addressId)
            if (response.isSuccessful) {
                AddressDataResult.Success(Unit)
            } else {
                AddressDataResult.Failure(
                    code = response.code(),
                    error = response.code().toSetDefaultAddressError(),
                    message = response.errorBody()?.string(),
                )
            }
        } catch (throwable: IOException) {
            AddressDataResult.Failure(error = SetDefaultAddressError.Network, message = throwable.message)
        } catch (throwable: Throwable) {
            AddressDataResult.Failure(error = SetDefaultAddressError.Unknown, message = throwable.message)
        }
    }

    suspend fun deleteAddress(customerId: Long, addressId: Long): AddressDataResult<Unit, DeleteAddressError> {
        return try {
            val response = networkDataSource.deleteAddress(config.apiVersion, customerId, addressId)
            if (response.isSuccessful) {
                AddressDataResult.Success(Unit)
            } else {
                AddressDataResult.Failure(
                    code = response.code(),
                    error = response.code().toDeleteAddressError(),
                    message = response.errorBody()?.string(),
                )
            }
        } catch (throwable: IOException) {
            AddressDataResult.Failure(error = DeleteAddressError.Network, message = throwable.message)
        } catch (throwable: Throwable) {
            AddressDataResult.Failure(error = DeleteAddressError.Unknown, message = throwable.message)
        }
    }

    private fun responseErrorToAddressError(code: Int, errorBody: String?): AddressCreationError {
        return when (code) {
            401 -> AddressCreationError.Unauthorized
            404 -> AddressCreationError.NotFound
            422 -> errorBody.toAddressValidationError()
            in 500..599 -> AddressCreationError.Server
            else -> AddressCreationError.Unknown
        }
    }

    private fun String?.toAddressValidationError(): AddressCreationError {
        if (this == null) return AddressCreationError.Validation
        return try {
            val gson = Gson()
            val mapType = object : TypeToken<Map<String, Map<String, List<String>>>>() {}.type
            val map: Map<String, Map<String, List<String>>> = gson.fromJson(this, mapType)
            val keys = map["errors"]?.keys ?: emptyList()

            when {
                keys.contains("address") -> AddressCreationError.AddressAlreadyExist
                keys.contains("country") -> AddressCreationError.InvalidCountry
                keys.contains("province") -> AddressCreationError.InvalidProvince
                else -> AddressCreationError.Validation
            }
        } catch (_: Throwable) {
            AddressCreationError.Validation
        }
    }

    private fun Int.toGetAddressesError(): GetAddressesError {
        return when (this) {
            401 -> GetAddressesError.Unauthorized
            404 -> GetAddressesError.NotFound
            in 500..599 -> GetAddressesError.Server
            else -> GetAddressesError.Unknown
        }
    }

    private fun Int.toSetDefaultAddressError(): SetDefaultAddressError {
        return when (this) {
            401 -> SetDefaultAddressError.Unauthorized
            404 -> SetDefaultAddressError.NotFound
            in 500..599 -> SetDefaultAddressError.Server
            else -> SetDefaultAddressError.Unknown
        }
    }

    private fun Int.toDeleteAddressError(): DeleteAddressError {
        return when (this) {
            401 -> DeleteAddressError.Unauthorized
            404 -> DeleteAddressError.NotFound
            422 -> DeleteAddressError.DefaultAddress
            in 500..599 -> DeleteAddressError.Server
            else -> DeleteAddressError.Unknown
        }
    }
}
