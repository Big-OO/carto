package com.shopify.carto.feature.login.data.dto

data class UserDto(
    val id: String,
    val email: String,
    val name: String?,
    val customerId: String?,
)
