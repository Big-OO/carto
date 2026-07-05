package com.shopify.carto.feature.addresses.data.remote.network

import com.shopify.carto.feature.addresses.data.remote.dto.AddressResponseDto
import com.shopify.carto.feature.addresses.data.remote.dto.AddressesResponseDto
import com.shopify.carto.feature.addresses.data.remote.dto.CreateAddressBodyDto
import retrofit2.Response

interface AddressesNetworkDataSource {
    suspend fun getAddresses(
        version: String,
        customerId: Long,
        limit: Int = 100,
    ): Response<AddressesResponseDto>

    suspend fun createAddress(
        version: String,
        customerId: Long,
        body: CreateAddressBodyDto,
    ): Response<AddressResponseDto>

    suspend fun setDefaultAddress(
        version: String,
        customerId: Long,
        addressId: Long,
    ): Response<Unit>

    suspend fun deleteAddress(
        version: String,
        customerId: Long,
        addressId: Long,
    ): Response<Unit>
}
