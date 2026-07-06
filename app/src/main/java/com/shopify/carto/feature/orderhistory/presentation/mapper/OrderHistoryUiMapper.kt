package com.shopify.carto.feature.orderhistory.presentation.mapper

import com.shopify.carto.feature.orderhistory.domain.model.Money
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryItem
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryStatus
import com.shopify.carto.feature.orderhistory.presentation.model.OrderHistoryItemUi
import com.shopify.carto.feature.orderhistory.presentation.model.OrderHistoryTabUi
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

fun OrderHistoryItem.toUi(): OrderHistoryItemUi {
    return OrderHistoryItemUi(
        id = id,
        name = name,
        date = createdAt.toDisplayDate(),
        subtotalBeforeDiscount = subtotalBeforeDiscount.format(),
        totalAfterDiscounts = totalAfterDiscounts.format(),
        totalDiscounts = totalDiscounts.format(),
        itemCount = itemCount.toString(),
        firstProductTitle = firstProductTitle.ifBlank { name },
        firstProductImageUrl = firstProductImageUrl,
        statusLabel = resolveStatusLabel(),
        tab = when (status) {
            OrderHistoryStatus.Ongoing -> OrderHistoryTabUi.Ongoing
            OrderHistoryStatus.Completed -> OrderHistoryTabUi.Completed
        },
    )
}

private fun OrderHistoryItem.resolveStatusLabel(): String {
    return when {
        fulfillmentStatus.equals("FULFILLED", ignoreCase = true) -> "Fulfilled"
        fulfillmentStatus.equals("PARTIALLY_FULFILLED", ignoreCase = true) -> "Partially fulfilled"
        financialStatus.equals("PAID", ignoreCase = true) -> "Paid"
        financialStatus.equals("PENDING", ignoreCase = true) -> "Pending"
        financialStatus.equals("AUTHORIZED", ignoreCase = true) -> "Authorized"
        else -> fulfillmentStatus.ifBlank { financialStatus.ifBlank { "Processing" } }
    }
}

private fun Money.format(): String {
    return runCatching {
        NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            currency = Currency.getInstance(currencyCode)
        }.format(amount)
    }.getOrElse {
        "$currencyCode ${"%.2f".format(Locale.US, amount)}"
    }
}

private fun String.toDisplayDate(): String {
    if (isBlank()) return ""
    return runCatching {
        OffsetDateTime.parse(this).format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault()))
    }.getOrElse { this }
}
