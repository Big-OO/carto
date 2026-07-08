package com.shopify.carto.feature.home.data.mappers

import com.shopify.carto.feature.home.data.model.CollectionDto
import com.shopify.carto.feature.home.domain.model.Category

private const val DASHBOARD_CATEGORY_SUFFIX = "dashboard-category"
private const val DASHBOARD_CATEGORY_MARKER = "<!-- dashboard:category -->"

fun CollectionDto.isDashboardCategory(): Boolean {
    return templateSuffix == DASHBOARD_CATEGORY_SUFFIX ||
            bodyHtml.orEmpty().contains(DASHBOARD_CATEGORY_MARKER, ignoreCase = true)
}

fun CollectionDto.toCategory() = Category(
    id = id,
    title = title,
    imageUrl = image?.src,
    description = bodyHtml.cleanDashboardDescription()
)

internal fun String?.cleanDashboardDescription(): String? {
    return this
        ?.replace(DASHBOARD_CATEGORY_MARKER, "")
        ?.replace("<!-- dashboard:brand -->", "")
        ?.replace("<!-- dashboard:style -->", "")
        ?.trim()
        ?.takeIf { it.isNotBlank() }
}
