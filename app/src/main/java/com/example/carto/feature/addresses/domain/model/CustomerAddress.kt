package com.example.carto.feature.addresses.domain.model

data class CustomerAddress(
    val id: Long,
    val nickname: String,
    val address1: String,
    val address2: String = "",
    val city: String,
    val province: String,
    val country: String,
    val zip: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val isDefault: Boolean,
)
