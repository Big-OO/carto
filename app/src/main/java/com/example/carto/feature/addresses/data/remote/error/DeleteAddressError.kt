package com.example.carto.feature.addresses.data.remote.error

sealed interface DeleteAddressError {
    data object Unauthorized : DeleteAddressError
    data object NotFound : DeleteAddressError
    data object Network : DeleteAddressError
    data object DefaultAddress : DeleteAddressError
    data object Server : DeleteAddressError
    data object Unknown : DeleteAddressError
}
