package com.shopify.carto.feature.search.data.remote

import com.shopify.carto.core.config.ShopifyConfig
import com.shopify.carto.feature.search.data.mapper.matchesKeyword
import com.shopify.carto.feature.search.data.mapper.toDomain
import com.shopify.carto.feature.search.data.result.SearchDataResult
import com.shopify.carto.feature.search.domain.model.SearchFailure
import com.shopify.carto.feature.search.domain.model.SearchFailureType
import com.shopify.carto.feature.search.domain.model.SearchProduct
import java.io.IOException
import javax.inject.Inject

class SearchProductRemoteDataSource @Inject constructor(
    private val api: SearchShopifyApi,
    private val config: ShopifyConfig,
) {
    suspend fun getInitialProducts(): SearchDataResult<List<SearchProduct>> {
        return fetchProducts(
            keyword = null,
            limit = INITIAL_PRODUCTS_LIMIT,
        )
    }

    suspend fun searchProducts(keyword: String): SearchDataResult<List<SearchProduct>> {
        return fetchProducts(
            keyword = keyword.trim().takeIf { it.isNotBlank() },
            limit = SEARCH_PRODUCTS_LIMIT,
        )
    }

    private suspend fun fetchProducts(
        keyword: String?,
        limit: Int,
    ): SearchDataResult<List<SearchProduct>> {
        if (!config.isValid) {
            return SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.ShopifyConfigurationMissing,
                    developerMessage = "Shopify search configuration is missing. hostname='${config.hostname}', apiVersion='${config.apiVersion}', hasToken=${config.adminAccessToken.isNotBlank()}.",
                )
            )
        }

        return try {
            val response = api.getProductsForSearch(
                version = config.apiVersion,
                limit = limit,
            )

            if (!response.isSuccessful) {
                return SearchDataResult.Failure(
                    SearchFailure(
                        type = response.code().toFailureType(),
                        developerMessage = "Shopify product search failed. code=${response.code()}, message=${response.message()}, errorBody=${response.errorBody()?.string().orEmpty()}.",
                    )
                )
            }

            val products = response.body()
                ?.products
                .orEmpty()
                .asSequence()
                .filter { product -> keyword == null || product.matchesKeyword(keyword) }
                .mapNotNull { it.toDomain() }
                .sortedBy { it.title.lowercase() }
                .toList()

            SearchDataResult.Success(products)
        } catch (exception: IOException) {
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.Network,
                    developerMessage = "Network failure while loading search products: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
                )
            )
        } catch (exception: Exception) {
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.Unknown,
                    developerMessage = "Unexpected failure while loading search products: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
                )
            )
        }
    }

    private fun Int.toFailureType(): SearchFailureType {
        return when (this) {
            401, 403 -> SearchFailureType.Unauthorized
            in 500..599 -> SearchFailureType.Server
            else -> SearchFailureType.Unknown
        }
    }

    private companion object {
        const val INITIAL_PRODUCTS_LIMIT = 20
        const val SEARCH_PRODUCTS_LIMIT = 250
    }
}
