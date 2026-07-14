package com.shopify.carto.feature.search.data.remote.networkoperation

import com.shopify.carto.feature.search.data.remote.model.SearchProductSuggestionsResponseDto

interface SearchNetworkOperation {
    suspend fun searchProductSuggestions(
        keyword: String,
        first: Int,
    ): SearchProductSuggestionsResponseDto
}
