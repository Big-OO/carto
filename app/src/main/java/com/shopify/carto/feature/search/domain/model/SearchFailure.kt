package com.shopify.carto.feature.search.domain.model

data class SearchFailure(
    val type: SearchFailureType,
    val developerMessage: String,
)
