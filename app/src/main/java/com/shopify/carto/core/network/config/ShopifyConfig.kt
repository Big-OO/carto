package com.shopify.carto.core.network.config

data class ShopifyConfig(
    val hostname: String,
    val apiVersion: String,
    val adminAccessToken: String,
    val storefrontAccessToken: String,
) {

    val adminRestBaseUrl: String
        get() = "https://$hostname/"

    val storefrontGraphQlUrl: String
        get() = "https://$hostname/api/$apiVersion/graphql.json"

    val adminGraphQlUrl: String
        get() = "https://$hostname/admin/api/$apiVersion/graphql.json"

    val isAdminRestValid: Boolean
        get() = hostname.isNotBlank() && apiVersion.isNotBlank() && adminAccessToken.isNotBlank()

    val isAdminGraphQlValid: Boolean
        get() = hostname.isNotBlank() && apiVersion.isNotBlank() && adminAccessToken.isNotBlank()

    val isStorefrontGraphQlValid: Boolean
        get() = hostname.isNotBlank() && apiVersion.isNotBlank() && storefrontAccessToken.isNotBlank()
}
