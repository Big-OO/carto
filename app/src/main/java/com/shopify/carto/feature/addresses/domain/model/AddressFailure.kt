package com.shopify.carto.feature.addresses.domain.model

enum class AddressFailureType {
    MissingCustomer,
    Network,
    Unauthorized,
    DefaultAddressDeletion,
    InvalidProvince,
    AddressAlreadyExist,
    InvalidCountry,
    Validation,
    NotFound,
    Server,
    Unknown,
}

data class AddressFailure(
    val type: AddressFailureType,
    val message: String? = null,
)
