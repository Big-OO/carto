package com.shopify.carto.feature.orderhistory.presentation.model

data class OrderHistoryItemUi(
    val id: String,
    val name: String,
    val date: String,
    val subtotalBeforeDiscount: String,
    val totalAfterDiscounts: String,
    val totalDiscounts: String,
    val itemCount: String,
    val firstProductTitle: String,
    val firstProductImageUrl: String?,
    val statusLabel: String,
    val tab: OrderHistoryTabUi,
)
