package com.example.carto.core.config

data class ShopifyConfig(
    val hostname: String,
    val apiVersion: String,
    val adminAccessToken: String,
) {
    val isValid: Boolean
        get() = hostname.isNotBlank() && apiVersion.isNotBlank() && adminAccessToken.isNotBlank()
}
