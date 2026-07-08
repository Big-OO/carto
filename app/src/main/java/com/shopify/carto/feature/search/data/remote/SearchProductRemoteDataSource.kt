package com.shopify.carto.feature.search.data.remote

import com.apollographql.apollo.exception.ApolloException
import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.feature.search.data.mapper.matchesCatalogKeyword
import com.shopify.carto.feature.search.data.mapper.toCatalogDomain
import com.shopify.carto.feature.search.data.mapper.toDomain
import com.shopify.carto.feature.search.data.remote.network.SearchNetworkDataSource
import com.shopify.carto.feature.search.data.remote.networkoperation.SearchNetworkOperation
import com.shopify.carto.feature.search.data.result.SearchDataResult
import com.shopify.carto.feature.search.domain.model.SearchCatalogProduct
import com.shopify.carto.feature.search.domain.model.SearchFailure
import com.shopify.carto.feature.search.domain.model.SearchFailureType
import com.shopify.carto.feature.search.domain.model.SearchProduct
import java.io.IOException
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class SearchProductRemoteDataSource @Inject constructor(
    private val networkOperation: SearchNetworkOperation,
    private val networkDataSource: SearchNetworkDataSource,
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

    suspend fun getInitialCatalogProducts(): SearchDataResult<List<SearchCatalogProduct>> {
        return fetchCatalogProducts(
            keyword = null,
            limit = INITIAL_CATALOG_PRODUCTS_LIMIT,
        )
    }

    suspend fun searchCatalogProducts(keyword: String): SearchDataResult<List<SearchCatalogProduct>> {
        return fetchCatalogProducts(
            keyword = keyword.trim().takeIf { it.isNotBlank() },
            limit = SEARCH_CATALOG_PRODUCTS_LIMIT,
        )
    }

    private suspend fun fetchCatalogProducts(
        keyword: String?,
        limit: Int,
    ): SearchDataResult<List<SearchCatalogProduct>> {
        if (!config.isAdminRestValid) {
            return SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.ShopifyConfigurationMissing,
                    developerMessage = "Shopify Admin REST search configuration is missing. hostname='${config.hostname}', apiVersion='${config.apiVersion}', hasToken=${config.adminAccessToken.isNotBlank()}.",
                ),
            )
        }

        return try {
            val response = networkDataSource.getProductsForSearch(
                version = config.apiVersion,
                limit = limit,
            )

            if (!response.isSuccessful) {
                return SearchDataResult.Failure(
                    SearchFailure(
                        type = response.code().toFailureType(),
                        developerMessage = "Shopify Admin REST product search failed. code=${response.code()}, message=${response.message()}, errorBody=${response.errorBody()?.string().orEmpty()}.",
                    ),
                )
            }

            val products = response.body()
                ?.products
                .orEmpty()
                .asSequence()
                .filter { product -> keyword == null || product.matchesCatalogKeyword(keyword) }
                .mapNotNull { it.toCatalogDomain() }
                .distinctBy { it.id }
                .sortedBy { it.title.lowercase() }
                .toList()

            SearchDataResult.Success(products)
        } catch (exception: IOException) {
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.Network,
                    developerMessage = "Network failure while loading Admin REST search products: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
                ),
            )
        } catch (exception: Exception) {
            if (exception is CancellationException) throw exception
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.Unknown,
                    developerMessage = "Unexpected failure while loading Admin REST search products: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
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

    private fun Int.toFailureType(): SearchFailureType {
        return when (this) {
            401, 403 -> SearchFailureType.Unauthorized
            in 500..599 -> SearchFailureType.Server
            else -> SearchFailureType.Unknown
        }
    }

    private companion object {
        const val SEARCH_SUGGESTIONS_LIMIT = 10
        const val INITIAL_CATALOG_PRODUCTS_LIMIT = 20
        const val SEARCH_CATALOG_PRODUCTS_LIMIT = 250
    }
}
