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
    private val nameParts: List<String>
        get() = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }

    val firstName: String
        get() = nameParts.firstOrNull().orEmpty()

    val lastName: String
        get() = nameParts.drop(1).joinToString(" ")
}
