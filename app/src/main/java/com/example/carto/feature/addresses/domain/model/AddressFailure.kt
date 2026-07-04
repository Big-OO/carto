package com.example.carto.feature.addresses.domain.model

enum class AddressFailureType {
    MissingCustomer,
    Network,
    InvalidProvince,
    AddressAlreadyExist,
    InvalidCountry,
    Validation,
    NotFound,
    Unknown,
}

data class AddressFailure(
    val type: AddressFailureType,
    val message: String? = null,
)
