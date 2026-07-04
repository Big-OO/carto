package com.example.carto.feature.addresses.data.repository

import com.example.carto.feature.addresses.data.remote.AddressesRemoteDataSource
import com.example.carto.feature.addresses.data.remote.CustomerIdDataSource
import com.example.carto.feature.addresses.data.remote.dto.AddressDto
import com.example.carto.feature.addresses.data.remote.mapper.toAddressCreationFailure
import com.example.carto.feature.addresses.data.remote.mapper.toAddressesFailure
import com.example.carto.feature.addresses.data.remote.mapper.toDefaultAddressFailure
import com.example.carto.feature.addresses.data.result.AddressDataResult
import com.example.carto.feature.addresses.domain.model.AddressFailure
import com.example.carto.feature.addresses.domain.model.AddressFailureType
import com.example.carto.feature.addresses.domain.model.AddressForm
import com.example.carto.feature.addresses.domain.model.AddressResult
import com.example.carto.feature.addresses.domain.model.CustomerAddress
import com.example.carto.feature.addresses.domain.repository.AddressesRepository
import javax.inject.Inject

class AddressesRepositoryImpl @Inject constructor(
    private val customerIdDataSource: CustomerIdDataSource,
    private val remoteDataSource: AddressesRemoteDataSource,
) : AddressesRepository {

    override suspend fun getAddresses(): AddressResult<List<CustomerAddress>> {
        val customerId = getCustomerIdOrFailure() ?: return missingCustomerFailure()

        return when (val result = remoteDataSource.getAddresses(customerId)) {
            is AddressDataResult.Success -> AddressResult.Success(result.data.map { it.toDomain() })
            is AddressDataResult.Failure -> AddressResult.Failure(result.toAddressesFailure())
        }
    }

    override suspend fun createAddress(form: AddressForm): AddressResult<CustomerAddress> {
        val customerId = getCustomerIdOrFailure() ?: return missingCustomerFailure()

        return when (val result = remoteDataSource.createAddress(customerId, form)) {
            is AddressDataResult.Success -> {
                if (form.isDefault) {
                    result.data.id?.let { remoteDataSource.setDefaultAddress(customerId, it) }
                }
                AddressResult.Success(result.data.toDomain())
            }

            is AddressDataResult.Failure -> AddressResult.Failure(result.toAddressCreationFailure())
        }
    }

    override suspend fun setDefaultAddress(addressId: Long): AddressResult<Unit> {
        val customerId = getCustomerIdOrFailure() ?: return missingCustomerFailure()

        return when (val result = remoteDataSource.setDefaultAddress(customerId, addressId)) {
            is AddressDataResult.Success -> AddressResult.Success(Unit)
            is AddressDataResult.Failure -> AddressResult.Failure(result.toDefaultAddressFailure())
        }
    }

    private suspend fun getCustomerIdOrFailure(): Long? {
        return when (val result = customerIdDataSource.getShopifyCustomerId()) {
            is AddressDataResult.Success -> result.data
            is AddressDataResult.Failure -> null
        }
    }

    private fun <T> missingCustomerFailure(): AddressResult<T> {
        return AddressResult.Failure(
            AddressFailure(
                type = AddressFailureType.MissingCustomer,
                message = "Missing Shopify customer id",
            )
        )
    }

    private fun AddressDto.toDomain(): CustomerAddress {
        return CustomerAddress(
            id = id ?: 0L,
            nickname = name ?: ("Address - " + "$id".substring(4)),
            address1 = address1.orEmpty(),
            address2 = address2.orEmpty(),
            city = city.orEmpty(),
            province = province.orEmpty(),
            country = country.orEmpty(),
            zip = zip.orEmpty(),
            phone = phone.orEmpty(),
            firstName = firstName.orEmpty(),
            lastName = lastName.orEmpty(),
            isDefault = isDefault == true,
        )
    }
}
