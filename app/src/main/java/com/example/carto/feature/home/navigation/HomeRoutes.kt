package com.example.carto.feature.home.navigation

import android.net.Uri

object HomeRoutes {
    const val AllProducts = "all_products"
    const val AllBrands = "all_brands"
    const val AllCategories = "all_categories"
    const val Search = "search"
    const val CategoryProducts = "category_products/{categoryId}/{categoryTitle}"

    fun categoryProducts(categoryId: Long, categoryTitle: String): String {
        return "category_products/$categoryId/${Uri.encode(categoryTitle)}"
    }
}
