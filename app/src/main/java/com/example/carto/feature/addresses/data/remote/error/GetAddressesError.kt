package com.example.carto.feature.addresses.data.remote.error

sealed interface GetAddressesError {
    data object UnKnown: GetAddressesError
}