package com.shopify.carto.feature.ai_integration.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.shopify.carto.feature.product_details.domain.usecase.GetProductDetailsUseCase
import com.shopify.carto.feature.search.domain.model.SearchResult
import com.shopify.carto.feature.search.domain.usecases.SearchCatalogProductsUseCase
import javax.inject.Inject

class SearchFunctions @Inject constructor(
    private val searchProductsUseCase: SearchCatalogProductsUseCase,
    private val getProductDetailsUseCase: GetProductDetailsUseCase
) {

    /**
     * Search products by keyword, query, brand, type, category, or other text criteria.
     *
     * Use this when user wants to find products, view items, browse products, or find items of a specific brand/category.
     *
     * @param query The search query or product name.
     * @return A list of matching products or a description of the result.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchProducts(appFunctionContext: AppFunctionContext, query: String): String {
        return when (val result = searchProductsUseCase(query)) {
            is SearchResult.Success -> {
                val products = result.data
                if (products.isEmpty()) {
                    "No products found matching '$query'."
                } else {
                    products.joinToString("\n") { product ->
                        "Product ID: ${product.id}\nTitle: ${product.title}\nPrice: ${product.price}\nVendor: ${product.vendor}\nImage URL: ${product.imageUrl.orEmpty()}\n---"
                    }
                }
            }
            is SearchResult.Failure -> {
                "Failed to search products."
            }
        }
    }

    /**
     * Retrieve detailed information for a specific product using its unique ID.
     *
     * Use this when the user clicks a product, asks for more info, details, price, availability, description, or colors/sizes of a product.
     *
     * @param productId The ID of the product.
     * @return Detailed product information.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getProductDetails(appFunctionContext: AppFunctionContext, productId: Long): String {
        val result = getProductDetailsUseCase(productId)
        return if (result.isSuccess) {
            val product = result.getOrThrow()
            "Product ID: ${product.id}\nTitle: ${product.title}\nDescription: ${product.description}\nVendor: ${product.vendor}\nPrice: ${product.price} ${product.currency}\nImage URL: ${product.images.firstOrNull().orEmpty()}\nSizes: ${product.sizes.joinToString()}\nColors: ${product.colors.joinToString()}\nIn Stock: ${product.isInStock}\nVariants:\n" +
                    product.variants.joinToString("\n") { variant ->
                        "Variant ID: ${variant.id}\nPrice: ${variant.price}\nSize: ${variant.size ?: "N/A"}\nColor: ${variant.color ?: "N/A"}\nAvailable: ${variant.isAvailable}\n---"
                    }
        } else {
            "Product with ID $productId not found."
        }
    }
}
