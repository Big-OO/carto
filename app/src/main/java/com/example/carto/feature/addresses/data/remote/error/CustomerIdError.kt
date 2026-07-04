package com.example.carto.feature.addresses.data.remote.error

sealed interface CustomerIdError {
    data object UnKnown: CustomerIdError
}