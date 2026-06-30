package com.example.carto.home.navigation


object HomeRoutes {

    const val AllProducts = "all_products"

    const val AllVendors = "all_vendors"

    const val ProductDetails = "product_detail/{productId}"

    const val CategoryProducts =
        "category_products/{categoryId}/{categoryTitle}"

    fun productDetails(productId: Long) =
        "product_detail/$productId"

    fun categoryProducts(
        categoryId: Long,
        categoryTitle: String
    ) =
        "category_products/$categoryId/$categoryTitle"

}
