package com.shopify.carto.feature.ai_integration.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.shopify.carto.feature.product_details.domain.usecase.GetProductDetailsUseCase
import javax.inject.Inject

class CompareFunctions @Inject constructor(
    private val getProductDetailsUseCase: GetProductDetailsUseCase
) {

    /**
     * Compare two products by their unique IDs.
     *
     * Use this when the user asks to compare two products, understand price or specification differences, or pick between two items.
     *
     * @param productId1 The ID of the first product.
     * @param productId2 The ID of the second product.
     * @return A detailed comparison.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun compareProducts(
        appFunctionContext: AppFunctionContext,
        productId1: Long,
        productId2: Long
    ): String {
        val result1 = getProductDetailsUseCase(productId1)
        val result2 = getProductDetailsUseCase(productId2)

        if (result1.isFailure || result2.isFailure) {
            return "Failed to fetch details for comparison. Ensure both product IDs are valid."
        }

        val p1 = result1.getOrThrow()
        val p2 = result2.getOrThrow()

        return """
            Comparison of: '${p1.title}' and '${p2.title}':
            
            | Feature | ${p1.title} | ${p2.title} |
            | --- | --- | --- |
            | Product ID | ${p1.id} | ${p2.id} |
            | Price | ${p1.price} ${p1.currency} | ${p2.price} ${p2.currency} |
            | Vendor | ${p1.vendor} | ${p2.vendor} |
            | Type | ${p1.productType} | ${p2.productType} |
            | Stock | ${if (p1.isInStock) "In Stock" else "Out of Stock"} | ${if (p2.isInStock) "In Stock" else "Out of Stock"} |
            | Sizes | ${p1.sizes.joinToString()} | ${p2.sizes.joinToString()} |
            | Colors | ${p1.colors.joinToString()} | ${p2.colors.joinToString()} |
            
            Description 1: ${p1.description}
            Description 2: ${p2.description}
        """.trimIndent()
    }
}
