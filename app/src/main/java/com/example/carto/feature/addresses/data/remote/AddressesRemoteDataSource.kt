package com.example.carto.feature.addresses.data.remote

import com.example.carto.core.config.ShopifyConfig
import com.example.carto.feature.addresses.data.remote.dto.AddressDto
import com.example.carto.feature.addresses.data.remote.dto.CreateAddressBodyDto
import com.example.carto.feature.addresses.data.remote.dto.CreateAddressDto
import com.example.carto.feature.addresses.data.remote.service.AddressesShopifyApi
import com.example.carto.feature.addresses.data.result.AddressDataResult
import com.example.carto.feature.addresses.domain.model.AddressForm
import javax.inject.Inject

class AddressesRemoteDataSource @Inject constructor(
    private val api: AddressesShopifyApi,
    private val config: ShopifyConfig,
) {
    suspend fun getAddresses(customerId: Long): AddressDataResult<List<AddressDto>> {
        return try {
            val response = api.getAddresses(config.apiVersion, customerId)
            if (response.isSuccessful) {
                AddressDataResult.Success(response.body()?.addresses.orEmpty())
            } else {
                AddressDataResult.Failure(response.code(), response.errorBody()?.string())
            }
        } catch (throwable: Throwable) {
            AddressDataResult.Failure(developerMessage = throwable.message)
        }
    }

    suspend fun createAddress(customerId: Long, form: AddressForm): AddressDataResult<AddressDto> {
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
                AddressDataResult.Failure(response.code(), response.errorBody()?.string())
            }
        } catch (throwable: Throwable) {
            AddressDataResult.Failure(developerMessage = throwable.message)
        }
    }

    suspend fun setDefaultAddress(customerId: Long, addressId: Long): AddressDataResult<Unit> {
        return try {
            val response = api.setDefaultAddress(config.apiVersion, customerId, addressId)
            if (response.isSuccessful) {
                AddressDataResult.Success(Unit)
            } else {
                AddressDataResult.Failure(response.code(), response.errorBody()?.string())
            }
        } catch (throwable: Throwable) {
            AddressDataResult.Failure(developerMessage = throwable.message)
        }
    }
}
