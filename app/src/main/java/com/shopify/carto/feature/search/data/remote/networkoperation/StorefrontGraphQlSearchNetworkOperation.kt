package com.shopify.carto.feature.search.data.remote.networkoperation

import com.apollographql.apollo.ApolloClient
import com.shopify.carto.core.graphql.shopify.SearchProductsQuery
import com.shopify.carto.core.network.graphql.dto.GraphQlErrorDto
import com.shopify.carto.core.network.qualifier.StorefrontApollo
import com.shopify.carto.feature.search.data.remote.model.SearchProductSuggestionDto
import com.shopify.carto.feature.search.data.remote.model.SearchProductSuggestionsResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StorefrontGraphQlSearchNetworkOperation @Inject constructor(
    @param:StorefrontApollo private val apolloClient: ApolloClient,
) : SearchNetworkOperation {

    override suspend fun searchProductSuggestions(
        keyword: String,
        first: Int,
    ): SearchProductSuggestionsResponseDto {
        val response = apolloClient.query(
            SearchProductsQuery(
                query = keyword,
                first = first,
            ),
        ).execute()

        return withContext(Dispatchers.Default) {
            SearchProductSuggestionsResponseDto(
                suggestions = response.data
                    ?.search
                    ?.nodes
                    .orEmpty()
                    .mapNotNull { node ->
                        node.onProduct?.let { product ->
                            SearchProductSuggestionDto(
                                id = product.id,
                                title = product.title,
                            )
                        }
                    },
                errors = response.errors.orEmpty().map { error ->
                    GraphQlErrorDto(message = error.message)
                },
            )
        }
    }
}
