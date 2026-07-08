package com.shopify.carto.feature.orderdetails.presentation.model

data class OrderDetailsUi(
    val id: String,
    val name: String,
    val date: String,
    val confirmationNumber: String,
    val paymentStatus: String,
    val fulfillmentStatus: String,
    val lifecycleStatus: String,
    val subtotal: String,
    val discount: String,
    val shipping: String,
    val tax: String,
    val total: String,
    val paymentMethods: String,
    val discountCodes: String,
    val shippingAddress: String,
    val canCancel: Boolean,
    val canHide: Boolean,
    val items: List<OrderDetailsLineItemUi>,
    val fulfillments: List<OrderDetailsFulfillmentUi>,
    val refunds: List<OrderDetailsRefundUi>,
)

data class OrderDetailsLineItemUi(
    val id: String,
    val title: String,
    val variantTitle: String,
    val sku: String,
    val vendor: String,
    val quantity: String,
    val imageUrl: String?,
    val originalUnitPrice: String,
    val discountedTotal: String,
    val totalDiscount: String,
)

data class OrderDetailsFulfillmentUi(
    val id: String,
    val status: String,
    val createdAt: String,
    val trackingNumber: String,
)

data class OrderDetailsRefundUi(
    val id: String,
    val createdAt: String,
)
