package com.example.carto.feature.addresses.data.remote

import com.example.carto.core.config.ShopifyConfig
import com.example.carto.feature.addresses.data.remote.dto.AddressDto
import com.example.carto.feature.addresses.data.remote.dto.CreateAddressBodyDto
import com.example.carto.feature.addresses.data.remote.dto.CreateAddressDto
import com.example.carto.feature.addresses.data.remote.error.AddressCreationError
import com.example.carto.feature.addresses.data.remote.error.GetAddressesError
import com.example.carto.feature.addresses.data.remote.error.SetDefaultAddressError
import com.example.carto.feature.addresses.data.remote.service.AddressesShopifyApi
import com.example.carto.feature.addresses.data.result.AddressDataResult
import com.example.carto.feature.addresses.domain.model.AddressForm
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import javax.inject.Inject

class AddressesRemoteDataSource @Inject constructor(
    private val api: AddressesShopifyApi,
    private val config: ShopifyConfig,
) {
    suspend fun getAddresses(customerId: Long): AddressDataResult<List<AddressDto>, GetAddressesError> {
        return try {
            val response = api.getAddresses(config.apiVersion, customerId)
            if (response.isSuccessful) {
                AddressDataResult.Success(response.body()?.addresses.orEmpty())
            } else {
                AddressDataResult.Failure(response.code())
            }
        } catch (_: Throwable) {
            AddressDataResult.Failure(error = GetAddressesError.UnKnown)
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
            val response = api.createAddress(config.apiVersion, customerId, body)
            val address = response.body()?.address
            if (response.isSuccessful && address != null) {
                AddressDataResult.Success(address)
            } else {
                AddressDataResult.Failure(
                    response.code(), responseErrorToAddressError(
                        response.errorBody()
                    )
                )
            }
        } catch (_: Throwable) {
            AddressDataResult.Failure(error = AddressCreationError.UnKnown)
        }
    }

    suspend fun setDefaultAddress(customerId: Long, addressId: Long): AddressDataResult<Unit, SetDefaultAddressError> {
        return try {
            val response = api.setDefaultAddress(config.apiVersion, customerId, addressId)
            if (response.isSuccessful) {
                AddressDataResult.Success(Unit)
            } else {
                AddressDataResult.Failure(
                    response.code(), SetDefaultAddressError.UnKnown
                )
            }
        } catch (_: Throwable) {
            AddressDataResult.Failure(error = SetDefaultAddressError.UnKnown)
        }
    }

    private fun responseErrorToAddressError(errorBody: ResponseBody?): AddressCreationError {
        if (errorBody == null) return AddressCreationError.UnKnown

        val jsonString = errorBody.string()

        val gson = Gson()
        val mapType = object : TypeToken<Map<String, Map<String, List<String>>>>() {}.type
        val map: Map<String, Map<String, List<String>>> = gson.fromJson(jsonString, mapType)

        val keys = map["errors"]?.keys ?: emptyList()

        return if (keys.contains("address")) {
            AddressCreationError.AddressAlreadyExist
        } else if (keys.contains("country")) {
            AddressCreationError.InvalidCountry
        } else {
            AddressCreationError.InvalidProvince
        }
    }
}
