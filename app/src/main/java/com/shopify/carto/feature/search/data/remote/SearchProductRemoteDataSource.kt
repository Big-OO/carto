package com.shopify.carto.feature.search.data.remote

import com.apollographql.apollo.exception.ApolloException
import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.feature.search.data.mapper.toDomain
import com.shopify.carto.feature.search.data.remote.networkoperation.SearchNetworkOperation
import com.shopify.carto.feature.search.data.result.SearchDataResult
import com.shopify.carto.feature.search.domain.model.SearchFailure
import com.shopify.carto.feature.search.domain.model.SearchFailureType
import com.shopify.carto.feature.search.domain.model.SearchProduct
import java.io.IOException
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class SearchProductRemoteDataSource @Inject constructor(
    private val networkOperation: SearchNetworkOperation,
    private val config: ShopifyConfig,
) {
    suspend fun searchProducts(keyword: String): SearchDataResult<List<SearchProduct>> {
        val cleanedKeyword = keyword.trim().takeIf { it.isNotBlank() }
            ?: return SearchDataResult.Success(emptyList())

        if (!config.isStorefrontGraphQlValid) {
            return SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.ShopifyConfigurationMissing,
                    developerMessage = "Shopify search Storefront GraphQL configuration is missing. hostname='${config.hostname}', apiVersion='${config.apiVersion}', hasStorefrontToken=${config.storefrontAccessToken.isNotBlank()}.",
                ),
            )
        }

        return try {
            val response = networkOperation.searchProductSuggestions(
                keyword = cleanedKeyword.toShopifySearchQuery(),
                first = SEARCH_SUGGESTIONS_LIMIT,
            )

            if (response.errors.isNotEmpty()) {
                return SearchDataResult.Failure(
                    SearchFailure(
                        type = SearchFailureType.Server,
                        developerMessage = "Shopify search GraphQL failed. errors=${response.errors.joinToString { it.message.orEmpty() }}.",
                    ),
                )
            }

            val suggestions = response.suggestions
                .mapNotNull { it.toDomain() }
                .distinctBy { it.id }
                .sortedBy { it.title.lowercase() }

            SearchDataResult.Success(suggestions)
        } catch (exception: ApolloException) {
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.Network,
                    developerMessage = "Apollo failure while loading search suggestions: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
                ),
            )
        } catch (exception: IOException) {
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.Network,
                    developerMessage = "Network failure while loading search suggestions: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
                ),
            )
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.Unknown,
                    developerMessage = "Unexpected failure while loading search suggestions: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
                ),
            )
        }
    }

    private fun String.toShopifySearchQuery(): String {
        return trim()
            .replace("\\", " ")
            .replace("\"", " ")
            .replace("'", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private companion object {
        const val SEARCH_SUGGESTIONS_LIMIT = 10
    }
}
