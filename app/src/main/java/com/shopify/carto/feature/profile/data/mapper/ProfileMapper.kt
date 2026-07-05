package com.shopify.carto.feature.profile.data.mapper

import com.shopify.carto.feature.profile.data.local.CustomerProfileEntity
import com.shopify.carto.feature.profile.data.remote.dto.ShopifyCustomerProfileDto
import com.shopify.carto.feature.profile.domain.model.CustomerProfile

fun CustomerProfileEntity.toDomain() = CustomerProfile(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phone = phone,
    ordersCount = ordersCount,
    totalSpent = totalSpent
)

fun ShopifyCustomerProfileDto.toEntity() = CustomerProfileEntity(
    id = id.toString(),
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    email = email.orEmpty(),
    phone = phone,
    ordersCount = ordersCount ?: 0,
    totalSpent = totalSpent ?: "0.00"
)