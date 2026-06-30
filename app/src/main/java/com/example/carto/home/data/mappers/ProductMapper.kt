package com.example.carto.home.data.mappers

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.carto.home.domain.model.Product
import com.example.carto.home.data.model.ProductDto
import java.time.Instant
import java.time.temporal.ChronoUnit

private const val NEW_ARRIVAL_WINDOW_DAYS = 14L
private const val LOW_STOCK_THRESHOLD = 5

@RequiresApi(Build.VERSION_CODES.O)
fun ProductDto.toProduct(): Product {
    Log.d("Mapper", "Start mapping product $title")
    try {
        Log.d("Mapper", "variants size = ${variants?.size ?: 0}")
    } catch (e: Exception) {
        Log.e("Mapper", "variants crashed", e)
    }

    try {
        Log.d("Mapper", "images size = ${images.size}")
    } catch (e: Exception) {
        Log.e("Mapper", "images crashed", e)
    }
    val firstVariant = variants?.firstOrNull()
    val price = firstVariant?.price?.toDoubleOrNull() ?: 0.0
    val compareAtPrice = firstVariant?.compareAtPrice?.toDoubleOrNull()
    val totalStock = variants?.sumOf { it.inventoryQuantity ?: 0 }

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
        variantCount = variants?.size ?: 0,
        totalStock = totalStock?:0,
        createdAt = createdAt,
        isNew = isNew,
        isOnSale = compareAtPrice != null && compareAtPrice > price,
        isLowStock = totalStock in 1..LOW_STOCK_THRESHOLD
    )
}