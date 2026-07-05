package com.shopify.carto.feature.addresses.data.remote.error

sealed interface SetDefaultAddressError {
    data object Unauthorized : SetDefaultAddressError
    data object NotFound : SetDefaultAddressError
    data object Network : SetDefaultAddressError
    data object Server : SetDefaultAddressError
    data object Unknown : SetDefaultAddressError
}
