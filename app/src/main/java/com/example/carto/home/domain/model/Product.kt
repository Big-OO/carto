package com.example.carto.home.domain.model

data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val compareAtPrice: Double?,
    val imageUrl: String?,
    val imageCount: Int,
    val vendor: String,
    val productType: String,
    val variantCount: Int,
    val totalStock: Int,
    val createdAt: String,
    val isNew: Boolean,
    val isOnSale: Boolean,
    val isLowStock: Boolean
)

data class VendorUi(
    val name: String,
    val productCount: Int,
    val mainCategory: String,
    val representativeImageUrl: String?
)



fun List<Product>.toVendorUiList(): List<VendorUi> =
    this.filter { it.vendor.isNotBlank() }
        .groupBy { it.vendor }
        .map { (vendorName, products) ->
            val dominantCategory = products
                .groupingBy { it.productType }
                .eachCount()
                .maxByOrNull { it.value }
                ?.key ?: "GENERAL"

            val representativeImage = products
                .filter { !it.imageUrl.isNullOrBlank() }
                .maxByOrNull { it.createdAt }
                ?.imageUrl

            VendorUi(
                name = vendorName,
                productCount = products.size,
                mainCategory = dominantCategory,
                representativeImageUrl = representativeImage
            )
        }
        .sortedByDescending { it.productCount }