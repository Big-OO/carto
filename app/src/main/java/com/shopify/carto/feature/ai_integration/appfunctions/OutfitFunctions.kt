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
        // Fetch all initial products in the store by performing a broad search
        val allProductsResult = searchProductsUseCase("")
        val products = if (allProductsResult is SearchResult.Success) allProductsResult.data else emptyList()

        if (products.isEmpty()) {
            return "Could not find any items in the store right now to generate an outfit."
        }

        // Determine target gender to strictly prevent mixing styles (e.g. men's shirt with women's footwear)
        val isWomenPreferred = preference.contains("women", ignoreCase = true) || 
                               preference.contains("woman", ignoreCase = true) ||
                               preference.contains("female", ignoreCase = true) ||
                               preference.contains("girl", ignoreCase = true) ||
                               preference.contains("lady", ignoreCase = true)
                       
        val isMenPreferred = !isWomenPreferred && (
            preference.contains("men", ignoreCase = true) ||
            preference.contains("man", ignoreCase = true) ||
            preference.contains("male", ignoreCase = true) ||
            preference.contains("boy", ignoreCase = true)
        )

        val targetGender = if (isWomenPreferred) {
            "women"
        } else if (isMenPreferred) {
            "men"
        } else {
            // Coherent styling: pick a single target gender randomly if none specified
            if (Math.random() < 0.5) "men" else "women"
        }

        val womenRegex = Regex("""\b(women|woman|female|girl|girls|lady|ladies|dress|skirt|blouse|heel|heels|womens)\b""", RegexOption.IGNORE_CASE)
        val menRegex = Regex("""\b(men|man|male|boy|boys|mens|gentleman)\b""", RegexOption.IGNORE_CASE)

        // Filter products strictly based on target gender
        val sourceProducts = products.filter { product ->
            val title = product.title
            val type = product.productType
            val vendor = product.vendor
            
            val isExplicitlyWomen = womenRegex.containsMatchIn(title) || 
                                    womenRegex.containsMatchIn(type) || 
                                    womenRegex.containsMatchIn(vendor)
                               
            val isExplicitlyMen = menRegex.containsMatchIn(title) || 
                                  menRegex.containsMatchIn(type) || 
                                  menRegex.containsMatchIn(vendor)

            if (targetGender == "women") {
                // For women outfit, allow explicit women items or unisex items, but strictly exclude explicit men items
                isExplicitlyWomen || (!isExplicitlyMen)
            } else {
                // For men outfit, allow explicit men items or unisex items, but strictly exclude explicit women items
                isExplicitlyMen || (!isExplicitlyWomen)
            }
        }

        // Categorize items into mutually exclusive buckets
        val shoes = sourceProducts.filter { product ->
            val title = product.title.lowercase()
            val type = product.productType.lowercase()
            title.contains("shoe") || title.contains("sneaker") || title.contains("boot") || 
            title.contains("sandal") || title.contains("slide") || title.contains("loafer") ||
            title.contains("footwear") ||
            type.contains("shoe") || type.contains("sneaker") || type.contains("footwear")
        }

        val bottoms = sourceProducts.filter { product ->
            if (shoes.contains(product)) return@filter false
            val title = product.title.lowercase()
            val type = product.productType.lowercase()
            title.contains("pant") || title.contains("jeans") || title.contains("trouser") || 
            title.contains("shorts") || title.contains("skirt") || title.contains("leggings") || 
            title.contains("sweatpants") ||
            type.contains("pant") || type.contains("jeans") || type.contains("trouser") || type.contains("shorts")
        }

        val tops = sourceProducts.filter { product ->
            if (shoes.contains(product) || bottoms.contains(product)) return@filter false
            val title = product.title.lowercase()
            val type = product.productType.lowercase()
            title.contains("shirt") || title.contains("top") || title.contains("hoodie") || 
            title.contains("jacket") || title.contains("coat") || title.contains("blouse") || 
            title.contains("pullover") || title.contains("cardigan") || title.contains("sweater") ||
            type.contains("shirt") || type.contains("top") || type.contains("hoodie") || type.contains("jacket")
        }

        // Randomly pick one item from each category
        val chosenTop = tops.shuffled().firstOrNull()
        val chosenBottom = bottoms.shuffled().firstOrNull()
        val chosenShoe = shoes.shuffled().firstOrNull()

        if (chosenTop == null && chosenBottom == null && chosenShoe == null) {
            return "Could not find matching clothing items for the '$preference' style right now."
        }

        val genderLabel = if (targetGender == "women") "Women's " else "Men's "

        return buildString {
            appendLine("Curated ${genderLabel}'$preference' Outfit Recommendation:")
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
