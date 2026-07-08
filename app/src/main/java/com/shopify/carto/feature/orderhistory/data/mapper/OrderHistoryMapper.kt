package com.shopify.carto.feature.orderhistory.data.mapper

import com.shopify.carto.feature.orderhistory.data.remote.dto.ImageDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.MoneySetDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryLineItemDto
import com.shopify.carto.feature.orderhistory.data.remote.dto.OrderHistoryOrderDto
import com.shopify.carto.feature.orderhistory.domain.model.Money
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryItem
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryStatus

fun OrderHistoryOrderDto.toDomain(): OrderHistoryItem? {
    val orderId = id?.takeIf { it.isNotBlank() } ?: return null
    val orderName = name?.takeIf { it.isNotBlank() } ?: orderId.substringAfterLast("/")
    val items = lineItems?.nodes.orEmpty()
    val firstLineItem = items.firstOrNull()
    val itemCount = items.sumOf { it.currentQuantity ?: it.quantity ?: 0 }.takeIf { it > 0 } ?: items.size
    val financial = displayFinancialStatus.orEmpty()
    val fulfillment = displayFulfillmentStatus.orEmpty()

    return OrderHistoryItem(
        id = orderId,
        name = orderName,
        createdAt = createdAt.orEmpty(),
        subtotalBeforeDiscount = currentSubtotalPriceSet.toDomainMoney(),
        totalAfterDiscounts = currentTotalPriceSet.toDomainMoney(),
        totalDiscounts = currentTotalDiscountsSet.toDomainMoney(),
        itemCount = itemCount,
        firstProductTitle = firstLineItem?.title
            ?.takeIf { it.isNotBlank() }
            ?: firstLineItem?.name.orEmpty(),
        firstProductImageUrl = firstLineItem?.firstImageUrl(),
        financialStatus = financial,
        fulfillmentStatus = fulfillment,
        status = resolveStatus(financial, fulfillment, cancelledAt),
    )
}

private fun MoneySetDto?.toDomainMoney(): Money {
    val money = this?.shopMoney
    return Money(
        amount = money?.amount?.toDoubleOrNull() ?: 0.0,
        currencyCode = money?.currencyCode.orEmpty().ifBlank { "USD" },
    )
}

private fun OrderHistoryLineItemDto.firstImageUrl(): String? {
    return listOfNotNull(
        image,
        variant?.image,
        variant?.product?.featuredImage,
        product?.featuredImage,
    ).firstValidUrl()
}

private fun List<ImageDto>.firstValidUrl(): String? {
    return firstOrNull { !it.url.isNullOrBlank() }?.url
}

private fun resolveStatus(
    financialStatus: String,
    fulfillmentStatus: String,
    cancelledAt: String?,
): OrderHistoryStatus {
    if (!cancelledAt.isNullOrBlank()) return OrderHistoryStatus.Completed

    val isPaid = financialStatus.equals("PAID", ignoreCase = true) ||
        financialStatus.equals("PARTIALLY_REFUNDED", ignoreCase = true) ||
        financialStatus.equals("REFUNDED", ignoreCase = true)

    val isFulfilled = fulfillmentStatus.equals("FULFILLED", ignoreCase = true)

    return if (isPaid && isFulfilled) {
        OrderHistoryStatus.Completed
    } else {
        OrderHistoryStatus.Ongoing
    }
}
