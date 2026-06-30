package com.example.carto.feature.login.data.mapper

import com.example.carto.feature.login.data.dto.UserDto
import com.example.carto.feature.login.domain.model.User
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDto() = UserDto(
    id = uid,
    email = email.orEmpty(),
    name = displayName
)

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        name = name
    )
}