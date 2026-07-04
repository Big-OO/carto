package com.shopify.carto.feature.addresses.domain.repository

import com.shopify.carto.feature.addresses.domain.model.AddressForm
import com.shopify.carto.feature.addresses.domain.model.AddressResult
import com.shopify.carto.feature.addresses.domain.model.CustomerAddress

interface AddressesRepository {
    suspend fun getAddresses(): AddressResult<List<CustomerAddress>>
    suspend fun createAddress(form: AddressForm): AddressResult<CustomerAddress>
    suspend fun setDefaultAddress(addressId: Long): AddressResult<Unit>
    suspend fun deleteAddress(addressId: Long): AddressResult<Unit>
}
