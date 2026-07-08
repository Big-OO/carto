package com.shopify.carto.feature.home.data.mappers

import com.shopify.carto.feature.home.data.model.CollectionDto
import com.shopify.carto.feature.home.domain.model.Brand

private const val DASHBOARD_BRAND_SUFFIX = "dashboard-brand"
private const val DASHBOARD_BRAND_MARKER = "<!-- dashboard:brand -->"
private const val DIESEL_FALLBACK_IMAGE = "https://logos-world.net/wp-content/uploads/2020/11/Diesel-Logo.png"

fun CollectionDto.isDashboardBrand(): Boolean {
    return templateSuffix == DASHBOARD_BRAND_SUFFIX ||
            bodyHtml.orEmpty().contains(DASHBOARD_BRAND_MARKER, ignoreCase = true)
}

fun CollectionDto.toBrand(): Brand {
    return Brand(
        id = id,
        name = title,
        imageUrl = resolveBrandImageUrl(title, image?.src),
        handle = handle
    )
}

fun resolveBrandImageUrl(brandName: String, imageUrl: String?): String? {
    val normalizedImageUrl = imageUrl?.trim()?.takeIf { it.isNotBlank() }
    val path = normalizedImageUrl
        ?.substringBefore("?")
        ?.lowercase()
        .orEmpty()

    return when {
        brandName.equals("Diesel", ignoreCase = true) && path.endsWith(".svg") -> DIESEL_FALLBACK_IMAGE
        brandName.equals("Diesel", ignoreCase = true) && normalizedImageUrl.isNullOrBlank() -> DIESEL_FALLBACK_IMAGE
        else -> normalizedImageUrl
    }
}
