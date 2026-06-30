package com.example.carto.search.data.remote

data class SearchShopifyConfig(
    val hostname: String,
    val apiVersion: String,
    val adminAccessToken: String,
) {
    val isValid: Boolean
        get() = hostname.isNotBlank() && apiVersion.isNotBlank() && adminAccessToken.isNotBlank()
}
