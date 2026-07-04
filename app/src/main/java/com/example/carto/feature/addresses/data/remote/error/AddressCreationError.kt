package com.example.carto.feature.addresses.data.remote.error

sealed interface AddressCreationError {
    data object InvalidCountry: AddressCreationError
    data object InvalidProvince: AddressCreationError
    data object AddressAlreadyExist: AddressCreationError
    data object UnKnown: AddressCreationError
}