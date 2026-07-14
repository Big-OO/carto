package com.shopify.carto.feature.addresses.domain.usecase

import com.shopify.carto.feature.addresses.domain.model.AddressFailure
import com.shopify.carto.feature.addresses.domain.model.AddressFailureType
import com.shopify.carto.feature.addresses.domain.model.AddressForm
import com.shopify.carto.feature.addresses.domain.model.AddressResult
import com.shopify.carto.feature.addresses.domain.model.CustomerAddress
import com.shopify.carto.feature.addresses.domain.repository.AddressesRepository
import javax.inject.Inject

class CreateAddressUseCase @Inject constructor(
    private val repository: AddressesRepository,
) {
    suspend operator fun invoke(form: AddressForm): AddressResult<CustomerAddress> {
        val isInvalid = form.name.isBlank() ||
            form.address1.isBlank() ||
            form.city.isBlank() ||
            form.province.isBlank() ||
            form.country.isBlank() ||
            form.zip.isBlank() ||
            form.firstName.isBlank()

        if (isInvalid) {
            return AddressResult.Failure(
                AddressFailure(
                    type = AddressFailureType.Validation,
                    message = "Required address fields are missing",
                )
            )
        }

        return repository.createAddress(form)
    }
}
