package com.shopify.carto.feature.payment.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from the Paymob Payment Intention API.
 * The key field is [clientSecret], which is used to launch the Paymob SDK.
 */
data class PaymobIntentionResponse(
    @SerializedName("client_secret")
    val clientSecret: String?,

    @SerializedName("id")
    val intentionId: String?,

    @SerializedName("payment_key")
    val paymentKey: String?,

    @SerializedName("confirmed")
    val confirmed: Boolean?,

    @SerializedName("status")
    val status: String?,
)
