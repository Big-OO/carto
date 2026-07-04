package com.example.carto.feature.addresses.data.remote.error

sealed interface SetDefaultAddressError {
    data object UnKnown: SetDefaultAddressError
}