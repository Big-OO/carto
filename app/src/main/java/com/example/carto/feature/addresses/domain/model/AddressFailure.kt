package com.example.carto.feature.addresses.domain.model

enum class AddressFailureType {
    MissingCustomer,
    Network,
    Validation,
    NotFound,
    Unknown,
}

data class AddressFailure(
    val type: AddressFailureType,
    val developerMessage: String? = null,
)
