package com.example.carto.feature.addresses.domain.model

data class AddressForm(
    val name: String = "",
    val address1: String = "",
    val city: String = "",
    val province: String = "",
    val country: String = "",
    val zip: String = "",
    val isDefault: Boolean = false,
) {
    val firstName: String = name.split(" ").getOrNull(0) ?: "Home"
    val lastName: String = name.split(" ").getOrNull(1) ?: ""
}
