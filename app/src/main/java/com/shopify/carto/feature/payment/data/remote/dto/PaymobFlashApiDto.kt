package com.shopify.carto.feature.payment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaymentMethodsResponse(
    @SerializedName("payment_methods")
    val paymentMethods: List<FlashPaymentMethod>?,
)

data class FlashPaymentMethod(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("type")
    val type: String?,
)

data class ConfirmPaymentRequest(
    @SerializedName("client_secret")
    val clientSecret: String,
    @SerializedName("payment_method")
    val paymentMethod: Int,
    @SerializedName("billing_data")
    val billingData: PaymobBillingData,
)

data class ConfirmPaymentResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("pending")
    val pending: Boolean?,
)
