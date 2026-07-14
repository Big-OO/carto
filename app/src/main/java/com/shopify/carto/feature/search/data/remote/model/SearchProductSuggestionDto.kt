package com.shopify.carto.feature.search.data.remote.model

import com.shopify.carto.core.network.graphql.dto.GraphQlErrorDto

data class SearchProductSuggestionDto(
    val id: String? = null,
    val title: String? = null,
)

data class SearchProductSuggestionsResponseDto(
    val suggestions: List<SearchProductSuggestionDto> = emptyList(),
    val errors: List<GraphQlErrorDto> = emptyList(),
)
