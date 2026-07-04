package com.example.carto.feature.addresses.data.remote.error

sealed interface GetAddressesError {
    data object Unauthorized : GetAddressesError
    data object NotFound : GetAddressesError
    data object Network : GetAddressesError
    data object Server : GetAddressesError
    data object Unknown : GetAddressesError
}
