package com.shopify.carto.feature.orderdetails.presentation.mapper

import com.shopify.carto.feature.orderdetails.domain.model.OrderDetails
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsFulfillment
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsLineItem
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsMoney
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsRefund
import com.shopify.carto.feature.orderdetails.presentation.model.OrderDetailsFulfillmentUi
import com.shopify.carto.feature.orderdetails.presentation.model.OrderDetailsLineItemUi
import com.shopify.carto.feature.orderdetails.presentation.model.OrderDetailsRefundUi
import com.shopify.carto.feature.orderdetails.presentation.model.OrderDetailsUi
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

fun OrderDetails.toUi(): OrderDetailsUi {
    return OrderDetailsUi(
        id = id,
        name = name,
        date = createdAt.toDisplayDate(),
        confirmationNumber = confirmationNumber.orEmpty().ifBlank { "-" },
        paymentStatus = financialStatus.toReadableStatus(),
        fulfillmentStatus = fulfillmentStatus.toReadableStatus(),
        lifecycleStatus = resolveLifecycleStatus(),
        subtotal = subtotal.format(),
        discount = totalDiscounts.format(),
        shipping = shipping.format(),
        tax = totalTax.format(),
        total = total.format(),
        paymentMethods = paymentGatewayNames.joinToString(", ").ifBlank { "-" },
        discountCodes = discountCodes.joinToString(", ").ifBlank { "-" },
        shippingAddress = shippingAddress?.let { address ->
            listOfNotNull(address.name, address.address1, address.city, address.country, address.phone)
                .filter { it.isNotBlank() }
                .joinToString("\n")
        }.orEmpty().ifBlank { "-" },
        canCancel = canCancel,
        canHide = canHide,
        items = lineItems.map { it.toUi() },
        fulfillments = fulfillments.map { it.toUi() },
        refunds = refunds.map { it.toUi() },
    )
}

private fun OrderDetailsLineItem.toUi(): OrderDetailsLineItemUi {
    return OrderDetailsLineItemUi(
        id = id,
        title = title.ifBlank { name },
        variantTitle = variantTitle.orEmpty().ifBlank { "-" },
        sku = sku.orEmpty().ifBlank { "-" },
        vendor = vendor.orEmpty().ifBlank { "-" },
        quantity = currentQuantity.toString(),
        imageUrl = imageUrl,
        originalUnitPrice = originalUnitPrice.format(),
        discountedTotal = discountedTotal.format(),
        totalDiscount = totalDiscount.format(),
    )
}

private fun OrderDetailsFulfillment.toUi(): OrderDetailsFulfillmentUi {
    return OrderDetailsFulfillmentUi(
        id = id,
        status = status.toReadableStatus(),
        createdAt = createdAt.orEmpty().toDisplayDate(),
        trackingNumber = trackingNumber.orEmpty().ifBlank { "-" },
    )
}

private fun OrderDetailsRefund.toUi(): OrderDetailsRefundUi {
    return OrderDetailsRefundUi(
        id = id,
        createdAt = createdAt.orEmpty().toDisplayDate(),
    )
}

private fun OrderDetails.resolveLifecycleStatus(): String {
    return when {
        isCancelled -> "Cancelled"
        !closedAt.isNullOrBlank() -> "Closed"
        else -> "Open"
    }
}

private fun String.toReadableStatus(): String {
    return lowercase()
        .split("_")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.replaceFirstChar { it.titlecase() } }
        .ifBlank { "-" }
}

private fun OrderDetailsMoney.format(): String {
    return runCatching {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            currency = Currency.getInstance(currencyCode)
        }.format(amount)
    }.getOrElse {
        "$currencyCode ${"%.2f".format(Locale.US, amount)}"
    }
}

private fun String.toDisplayDate(): String {
    if (isBlank()) return "-"
    return runCatching {
        OffsetDateTime.parse(this).format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))
    }.getOrElse { this }
}
