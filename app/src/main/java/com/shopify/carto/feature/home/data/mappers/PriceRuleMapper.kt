package com.shopify.carto.feature.home.data.mappers

import com.shopify.carto.feature.home.data.model.PriceRuleDto
import com.shopify.carto.feature.home.domain.model.Coupon

fun PriceRuleDto.toCoupon(): Coupon = Coupon(
    id = id,
    code = title.orEmpty(),
    valueType = valueType.orEmpty(),
    value = value?.toDoubleOrNull() ?: 0.0,
    usageLimit = usageLimit,
    oncePerCustomer = oncePerCustomer ?: false,
    startsAt = startsAt,
    endsAt = endsAt,
)
