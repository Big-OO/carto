package com.shopify.carto.feature.payment.domain.model

data class PromoCodeResult(
    val isValid: Boolean,
    val code: String = "",
    val discountAmountCents: Int = 0,
    val errorMessage: String? = null,
)
