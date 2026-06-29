package com.example.carto.registration.domain.repository

interface RegisterRepository {
    suspend fun register(fullName: String, email: String, password: String)
}