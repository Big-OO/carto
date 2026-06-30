package com.example.carto.feature.login.data.mapper

import com.example.carto.feature.login.data.dto.UserDto
import com.example.carto.feature.login.domain.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDto(customerId: String?) = UserDto(
    id = uid,
    email = email.orEmpty(),
    name = displayName,
    customerId = customerId,
)

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        name = name,
        customerId = customerId,
    )
}
