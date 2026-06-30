package com.example.carto.search.data.remote

import com.example.carto.search.data.mapper.matchesKeyword
import com.example.carto.search.data.mapper.toDomain
import com.example.carto.search.data.result.SearchDataResult
import com.example.carto.search.domain.model.SearchFailure
import com.example.carto.search.domain.model.SearchFailureType
import com.example.carto.search.domain.model.SearchProduct
import java.io.IOException
import javax.inject.Inject

class SearchProductRemoteDataSource @Inject constructor(
    private val api: SearchShopifyApi,
    private val config: SearchShopifyConfig,
) {
    suspend fun searchProducts(keyword: String): SearchDataResult<List<SearchProduct>> {
        if (!config.isValid) {
            return SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.ShopifyConfigurationMissing,
                    developerMessage = "Shopify search configuration is missing. hostname='${config.hostname}', apiVersion='${config.apiVersion}', hasToken=${config.adminAccessToken.isNotBlank()}.",
                )
            )
        }

        return try {
            val response = api.getProductsForSearch(version = config.apiVersion)

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
                .filter { it.matchesKeyword(keyword) }
                .mapNotNull { it.toDomain() }
                .sortedBy { it.title.lowercase() }

            SearchDataResult.Success(products)
        } catch (exception: IOException) {
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.Network,
                    developerMessage = "Network failure while searching products: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
                )
            )
        } catch (exception: Exception) {
            SearchDataResult.Failure(
                SearchFailure(
                    type = SearchFailureType.Unknown,
                    developerMessage = "Unexpected failure while searching products: ${exception::class.java.name}. ${exception.message.orEmpty().ifBlank { "No message provided." }}",
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
}
