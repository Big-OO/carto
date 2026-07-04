package com.shopify.carto.feature.addresses.domain.usecase

import com.shopify.carto.feature.addresses.domain.model.AddressFailure
import com.shopify.carto.feature.addresses.domain.model.AddressFailureType
import com.shopify.carto.feature.addresses.domain.model.AddressResult
import com.shopify.carto.feature.addresses.domain.repository.AddressesRepository
import javax.inject.Inject

class DeleteAddressUseCase @Inject constructor(
    private val repository: AddressesRepository,
) {
    suspend operator fun invoke(addressId: Long): AddressResult<Unit> {
        if (addressId <= 0L) {
            return AddressResult.Failure(
                AddressFailure(
                    type = AddressFailureType.Validation,
                    message = "Invalid address id",
                )
            )
        }

        return repository.deleteAddress(addressId)
    }
}
