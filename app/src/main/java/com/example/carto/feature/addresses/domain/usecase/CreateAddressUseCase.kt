package com.example.carto.feature.addresses.domain.usecase

import com.example.carto.feature.addresses.domain.model.AddressForm
import com.example.carto.feature.addresses.domain.model.AddressFailure
import com.example.carto.feature.addresses.domain.model.AddressFailureType
import com.example.carto.feature.addresses.domain.model.AddressResult
import com.example.carto.feature.addresses.domain.repository.AddressesRepository
import javax.inject.Inject

class CreateAddressUseCase @Inject constructor(
    private val repository: AddressesRepository,
) {
    suspend operator fun invoke(form: AddressForm): AddressResult<com.example.carto.feature.addresses.domain.model.CustomerAddress> {
        if (form.address1.isBlank() || form.name.split(" ").size < 2) {
            return AddressResult.Failure(
                AddressFailure(
                    type = AddressFailureType.Validation,
                    developerMessage = "Address and phone are required",
                )
            )
        }
        return repository.createAddress(form)
    }
}
