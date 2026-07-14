package com.shopify.carto.core.network.graphql.dto

data class GraphQlRequestDto(
    val query: String,
    val variables: Map<String, Any?> = emptyMap(),
)
