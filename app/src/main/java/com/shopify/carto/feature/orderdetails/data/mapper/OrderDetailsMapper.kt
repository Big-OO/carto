package com.shopify.carto.feature.orderdetails.data.mapper

import com.shopify.carto.feature.orderdetails.data.remote.dto.AddressDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.FulfillmentDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.ImageDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.MoneySetDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderDetailsOrderDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.OrderLineItemDto
import com.shopify.carto.feature.orderdetails.data.remote.dto.RefundDto
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetails
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsAddress
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsFulfillment
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsLineItem
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsMoney
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsRefund

fun OrderDetailsOrderDto.toDomain(): OrderDetails? {
    val orderId = id?.takeIf { it.isNotBlank() } ?: return null

    return OrderDetails(
        id = orderId,
        name = name.orEmpty().ifBlank { orderId.substringAfterLast("/") },
        createdAt = createdAt.orEmpty(),
        confirmationNumber = confirmationNumber,
        financialStatus = displayFinancialStatus.orEmpty(),
        fulfillmentStatus = displayFulfillmentStatus.orEmpty(),
        cancelledAt = cancelledAt,
        closedAt = closedAt,
        subtotal = currentSubtotalPriceSet.toDomainMoney(),
        totalDiscounts = currentTotalDiscountsSet.toDomainMoney(),
        totalTax = currentTotalTaxSet.toDomainMoney(),
        shipping = currentShippingPriceSet.toDomainMoney(),
        total = currentTotalPriceSet.toDomainMoney(),
        paymentGatewayNames = paymentGatewayNames.orEmpty(),
        discountCodes = discountCodes.orEmpty(),
        shippingAddress = shippingAddress?.toDomain(),
        lineItems = lineItems?.nodes.orEmpty().mapNotNull { it.toDomain() },
        fulfillments = fulfillments.orEmpty().mapNotNull { it.toDomain() },
        refunds = refunds.orEmpty().mapNotNull { it.toDomain() },
    )
}

private fun OrderLineItemDto.toDomain(): OrderDetailsLineItem? {
    val lineId = id?.takeIf { it.isNotBlank() } ?: return null
    return OrderDetailsLineItem(
        id = lineId,
        title = title.orEmpty().ifBlank { name.orEmpty() },
        name = name.orEmpty(),
        sku = sku,
        vendor = vendor,
        variantTitle = variantTitle,
        quantity = quantity ?: 0,
        currentQuantity = currentQuantity ?: quantity ?: 0,
        imageUrl = firstImageUrl(),
        originalUnitPrice = originalUnitPriceSet.toDomainMoney(),
        discountedTotal = discountedTotalSet.toDomainMoney(),
        totalDiscount = totalDiscountSet.toDomainMoney(),
    )
}

private fun AddressDto.toDomain(): OrderDetailsAddress {
    return OrderDetailsAddress(
        name = name,
        address1 = address1,
        city = city,
        country = country,
        phone = phone,
    )
}

private fun FulfillmentDto.toDomain(): OrderDetailsFulfillment? {
    val fulfillmentId = id?.takeIf { it.isNotBlank() } ?: return null
    val firstTrackingInfo = trackingInfo.orEmpty().firstOrNull()
    return OrderDetailsFulfillment(
        id = fulfillmentId,
        status = status.orEmpty(),
        createdAt = createdAt,
        trackingNumber = firstTrackingInfo?.number,
        trackingUrl = firstTrackingInfo?.url,
    )
}

private fun RefundDto.toDomain(): OrderDetailsRefund? {
    val refundId = id?.takeIf { it.isNotBlank() } ?: return null
    return OrderDetailsRefund(
        id = refundId,
        createdAt = createdAt,
    )
}

private fun MoneySetDto?.toDomainMoney(): OrderDetailsMoney {
    val money = this?.shopMoney
    return OrderDetailsMoney(
        amount = money?.amount?.toDoubleOrNull() ?: 0.0,
        currencyCode = money?.currencyCode.orEmpty().ifBlank { "USD" },
    )
}

private fun OrderLineItemDto.firstImageUrl(): String? {
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
