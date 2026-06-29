package com.example.carto.home.domain.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.carto.network.model.ProductDto
import java.time.Instant
import java.time.temporal.ChronoUnit

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

private const val NEW_ARRIVAL_WINDOW_DAYS = 14L
private const val LOW_STOCK_THRESHOLD = 5

@RequiresApi(Build.VERSION_CODES.O)
fun ProductDto.toProduct(): Product {
    val firstVariant = variants.firstOrNull()
    val price = firstVariant?.price?.toDoubleOrNull() ?: 0.0
    val compareAtPrice = firstVariant?.compareAtPrice?.toDoubleOrNull()
    val totalStock = variants.sumOf { it.inventoryQuantity ?: 0 }

    val isNew = runCatching {
        val created = Instant.parse(createdAt)
        ChronoUnit.DAYS.between(created, Instant.now()) <= NEW_ARRIVAL_WINDOW_DAYS
    }.getOrDefault(false)

    return Product(
        id = id,
        name = title,
        price = price,
        compareAtPrice = compareAtPrice?.takeIf { it > price },
        imageUrl = images.firstOrNull()?.src,
        imageCount = images.size,
        vendor = vendor,
        productType = productType,
        variantCount = variants.size,
        totalStock = totalStock,
        createdAt = createdAt,
        isNew = isNew,
        isOnSale = compareAtPrice != null && compareAtPrice > price,
        isLowStock = totalStock in 1..LOW_STOCK_THRESHOLD
    )
}

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