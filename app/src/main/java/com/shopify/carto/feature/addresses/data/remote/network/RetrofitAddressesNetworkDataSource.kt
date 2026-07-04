package com.shopify.carto.feature.addresses.data.remote.network

import com.shopify.carto.feature.addresses.data.remote.dto.AddressResponseDto
import com.shopify.carto.feature.addresses.data.remote.dto.AddressesResponseDto
import com.shopify.carto.feature.addresses.data.remote.dto.CreateAddressBodyDto
import com.shopify.carto.feature.addresses.data.remote.service.AddressesShopifyApi
import retrofit2.Response
import javax.inject.Inject

class RetrofitAddressesNetworkDataSource @Inject constructor(
    private val api: AddressesShopifyApi,
) : AddressesNetworkDataSource {

    override suspend fun getAddresses(
        version: String,
        customerId: Long,
        limit: Int,
    ): Response<AddressesResponseDto> {
        return api.getAddresses(version, customerId, limit)
    }

    override suspend fun createAddress(
        version: String,
        customerId: Long,
        body: CreateAddressBodyDto,
    ): Response<AddressResponseDto> {
        return api.createAddress(version, customerId, body)
    }

    override suspend fun setDefaultAddress(
        version: String,
        customerId: Long,
        addressId: Long,
    ): Response<Unit> {
        return api.setDefaultAddress(version, customerId, addressId)
    }

    override suspend fun deleteAddress(
        version: String,
        customerId: Long,
        addressId: Long,
    ): Response<Unit> {
        return api.deleteAddress(version, customerId, addressId)
    }
}
