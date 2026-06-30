package com.example.carto.search.domain.model

data class SearchFailure(
    val type: SearchFailureType,
    val developerMessage: String,
)
