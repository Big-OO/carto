package com.example.carto.feature.addresses.data.remote.error

sealed interface CustomerIdError {
    data object MissingCustomer : CustomerIdError
    data object Network : CustomerIdError
    data object Unknown : CustomerIdError
}
