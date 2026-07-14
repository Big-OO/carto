package com.shopify.carto.feature.addresses.data.remote.error

sealed interface AddressCreationError {
    data object InvalidCountry : AddressCreationError
    data object InvalidProvince : AddressCreationError
    data object AddressAlreadyExist : AddressCreationError
    data object Unauthorized : AddressCreationError
    data object NotFound : AddressCreationError
    data object Validation : AddressCreationError
    data object Network : AddressCreationError
    data object Server : AddressCreationError
    data object Unknown : AddressCreationError
}
