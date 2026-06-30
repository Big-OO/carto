package com.example.carto.feature.home.navigation

import android.net.Uri

object HomeRoutes {
    const val AllProducts = "all_products"
    const val AllVendors = "all_vendors"
    const val Search = "search"
    const val CategoryProducts = "category_products/{categoryId}/{categoryTitle}"

    fun categoryProducts(categoryId: Long, categoryTitle: String): String {
        return "category_products/$categoryId/${Uri.encode(categoryTitle)}"
    }
}
