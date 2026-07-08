package com.shopify.carto.feature.orderhistory.domain.model

data class OrderHistoryItem(
    val id: String,
    val name: String,
    val createdAt: String,
    val subtotalBeforeDiscount: Money,
    val totalAfterDiscounts: Money,
    val totalDiscounts: Money,
    val itemCount: Int,
    val firstProductTitle: String,
    val firstProductImageUrl: String?,
    val financialStatus: String,
    val fulfillmentStatus: String,
    val status: OrderHistoryStatus,
)
