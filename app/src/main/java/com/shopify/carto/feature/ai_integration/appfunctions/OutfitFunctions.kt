package com.shopify.carto.feature.ai_integration.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.shopify.carto.feature.search.domain.usecases.SearchProductsUseCase
import com.shopify.carto.feature.search.domain.model.SearchResult
import javax.inject.Inject

class OutfitFunctions @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase
) {

    /**
     * Generate a fashion outfit recommendation based on a style preference or theme.
     *
     * Use this when the user wants outfit suggestions, wants to dress up for an occasion, match clothes, or get styling advice.
     *
     * @param preference The style or occasion theme (e.g., "casual", "sporty", "winter", "party"). Defaults to "casual".
     * @return A list of matching outfit components.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun generateOutfit(
        appFunctionContext: AppFunctionContext,
        preference: String
    ): String {
        val topResult = searchProductsUseCase(if (preference.contains("sport", ignoreCase = true)) "t-shirt" else "hoodie")
        val bottomResult = searchProductsUseCase(if (preference.contains("sport", ignoreCase = true)) "pants" else "jeans")
        val shoeResult = searchProductsUseCase("shoes")

        val tops = if (topResult is SearchResult.Success) topResult.data else emptyList()
        val bottoms = if (bottomResult is SearchResult.Success) bottomResult.data else emptyList()
        val shoes = if (shoeResult is SearchResult.Success) shoeResult.data else emptyList()

        val chosenTop = tops.firstOrNull()
        val chosenBottom = bottoms.firstOrNull()
        val chosenShoe = shoes.firstOrNull()

        if (chosenTop == null && chosenBottom == null && chosenShoe == null) {
            return "Could not find matching clothing items for the '$preference' style right now."
        }

        return buildString {
            appendLine("Curated '$preference' Outfit Recommendation:")
            if (chosenTop != null) {
                appendLine("- Top: ${chosenTop.title} (ID: ${chosenTop.id}) by ${chosenTop.vendor} - ${chosenTop.price} EGP")
            }
            if (chosenBottom != null) {
                appendLine("- Bottom: ${chosenBottom.title} (ID: ${chosenBottom.id}) by ${chosenBottom.vendor} - ${chosenBottom.price} EGP")
            }
            if (chosenShoe != null) {
                appendLine("- Footwear: ${chosenShoe.title} (ID: ${chosenShoe.id}) by ${chosenShoe.vendor} - ${chosenShoe.price} EGP")
            }
            val total = (chosenTop?.price ?: 0.0) + (chosenBottom?.price ?: 0.0) + (chosenShoe?.price ?: 0.0)
            appendLine("Total Outfit Price: $total EGP")
        }
    }
}
