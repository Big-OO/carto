package com.example.carto.feature.login.domain.repository

interface LoginRepository {
    fun login(username: String, password: String)
}