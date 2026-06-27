package com.example.carto.feature.login.domain.datasource

interface LoginRemoteDataSource {
    fun login(
        email: String,
        password: String
    )
}