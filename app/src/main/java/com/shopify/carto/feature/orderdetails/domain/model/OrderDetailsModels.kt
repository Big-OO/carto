package com.shopify.carto.feature.orderdetails.domain.model

data class OrderDetails(
    val id: String,
    val name: String,
    val createdAt: String,
    val confirmationNumber: String?,
    val financialStatus: String,
    val fulfillmentStatus: String,
    val cancelledAt: String?,
    val closedAt: String?,
    val subtotal: OrderDetailsMoney,
    val totalDiscounts: OrderDetailsMoney,
    val totalTax: OrderDetailsMoney,
    val shipping: OrderDetailsMoney,
    val total: OrderDetailsMoney,
    val paymentGatewayNames: List<String>,
    val discountCodes: List<String>,
    val shippingAddress: OrderDetailsAddress?,
    val lineItems: List<OrderDetailsLineItem>,
    val fulfillments: List<OrderDetailsFulfillment>,
    val refunds: List<OrderDetailsRefund>,
) {
    val isCompleted: Boolean
        get() = isPaid && isFulfilled

    val isPaid: Boolean
        get() = financialStatus.equals("PAID", ignoreCase = true) ||
            financialStatus.equals("PARTIALLY_REFUNDED", ignoreCase = true) ||
            financialStatus.equals("REFUNDED", ignoreCase = true)

    val isFulfilled: Boolean
        get() = fulfillmentStatus.equals("FULFILLED", ignoreCase = true)

    val isCancelled: Boolean
        get() = !cancelledAt.isNullOrBlank()

    val canCancel: Boolean
        get() = !isCompleted && !isCancelled

    val canHide: Boolean
        get() = isCompleted || isCancelled
}

data class OrderDetailsLineItem(
    val id: String,
    val title: String,
    val name: String,
    val sku: String?,
    val vendor: String?,
    val variantTitle: String?,
    val quantity: Int,
    val currentQuantity: Int,
    val imageUrl: String?,
    val originalUnitPrice: OrderDetailsMoney,
    val discountedTotal: OrderDetailsMoney,
    val totalDiscount: OrderDetailsMoney,
)

data class OrderDetailsMoney(
    val amount: Double,
    val currencyCode: String,
)

data class OrderDetailsAddress(
    val name: String?,
    val address1: String?,
    val city: String?,
    val country: String?,
    val phone: String?,
)

data class OrderDetailsFulfillment(
    val id: String,
    val status: String,
    val createdAt: String?,
    val trackingNumber: String?,
    val trackingUrl: String?,
)

data class OrderDetailsRefund(
    val id: String,
    val createdAt: String?,
)
