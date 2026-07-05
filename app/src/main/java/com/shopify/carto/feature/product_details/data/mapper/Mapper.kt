package com.shopify.carto.feature.product_details.data.mapper

import com.shopify.carto.core.common.exception.DataException
import com.shopify.carto.feature.product_details.data.dto.ProductDetailsDto
import com.shopify.carto.feature.product_details.data.dto.ProductVariantDto
import com.shopify.carto.feature.product_details.domain.model.Product
import retrofit2.HttpException

private const val SIZE_OPTION_NAME = "Size"
private const val COLOR_OPTION_NAME = "Color"
private const val DEFAULT_CURRENCY = "EGP"

fun ProductDetailsDto.toDomain(): Product {
    val firstVariant = variants.firstOrNull()
    val sizeOption = options.firstOrNull { it.name.equals(SIZE_OPTION_NAME, ignoreCase = true) }
    val colorOption = options.firstOrNull { it.name.equals(COLOR_OPTION_NAME, ignoreCase = true) }

    return Product(
        id = id,
        title = title,
        description = bodyHtml.orEmpty().replace(Regex("<[^>]*>"), "").trim(),
        vendor = vendor,
        productType = productType,
        price = firstVariant?.price?.toDoubleOrNull() ?: 0.0,
        compareAtPrice = firstVariant?.compareAtPrice?.toDoubleOrNull(),
        currency = DEFAULT_CURRENCY,
        images = images.sortedBy { it.position }.map { it.src },
        sizes = sizeOption?.values.orEmpty(),
        colors = colorOption?.values.orEmpty(),
        variants = variants.map { it.toDomain(sizeOption?.position, colorOption?.position) },
        isInStock = variants.sumOf { it.inventoryQuantity } > 0
    )
}

private fun ProductVariantDto.toDomain(sizePosition: Int?, colorPosition: Int?): Product.ProductVariant {
    return Product.ProductVariant(
        id = id,
        price = price.toDoubleOrNull() ?: 0.0,
        compareAtPrice = compareAtPrice?.toDoubleOrNull(),
        size = optionValue(sizePosition),
        color = optionValue(colorPosition),
        isAvailable = inventoryQuantity > 0
    )
}

private fun ProductVariantDto.optionValue(position: Int?): String? {
    return when (position) {
        1 -> option1
        2 -> option2
        3 -> option3
        else -> null
    }
}

fun HttpException.toDomainException(productId: Long): DataException {
    return when (code()) {
        404 -> DataException.NotFound(productId.toString())
        in 500..599 -> DataException.Server(this)
        else -> DataException.Unknown(this)
    }
}