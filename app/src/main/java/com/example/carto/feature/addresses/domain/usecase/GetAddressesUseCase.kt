package com.example.carto.feature.addresses.domain.usecase

import com.example.carto.feature.addresses.domain.repository.AddressesRepository
import javax.inject.Inject

class GetAddressesUseCase @Inject constructor(
    private val repository: AddressesRepository,
) {
    suspend operator fun invoke() = repository.getAddresses()
}
