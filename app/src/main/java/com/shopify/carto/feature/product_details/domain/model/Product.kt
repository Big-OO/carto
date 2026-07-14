package com.shopify.carto.feature.product_details.domain.model

import com.shopify.carto.feature.product_details.domain.model.Product.ProductVariant

val ProductVariant.merchandiseId: String
    get() = "gid://shopify/ProductVariant/$id"
data class Product(
    val id: Long,
    val title: String,
    val description: String,
    val vendor: String,
    val productType: String,
    val price: Double,
    val compareAtPrice: Double?,
    val currency: String,
    val images: List<String>,
    val sizes: List<String>,
    val colors: List<String>,
    val variants: List<ProductVariant>,
    val isInStock: Boolean
) {
    data class ProductVariant(
        val id: Long,
        val price: Double,
        val compareAtPrice: Double?,
        val size: String?,
        val color: String?,
        val isAvailable: Boolean
    )

    fun findVariant(size: String?, color: String?): ProductVariant? {
        return variants.firstOrNull {
            (size == null || it.size == size) && (color == null || it.color == color)
        }
    }


}