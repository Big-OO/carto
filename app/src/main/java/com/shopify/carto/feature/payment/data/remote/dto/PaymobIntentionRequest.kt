package com.shopify.carto.feature.payment.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request body for the Paymob Payment Intention API.
 * POST https://accept.paymob.com/v1/intention/
 */
data class PaymobIntentionRequest(
    @SerializedName("amount")
    val amount: Int,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("payment_methods")
    val paymentMethods: List<Int>,

    @SerializedName("items")
    val items: List<PaymobItem>,

    @SerializedName("billing_data")
    val billingData: PaymobBillingData,
)

data class PaymobItem(
    @SerializedName("name")
    val name: String,

    @SerializedName("amount")
    val amount: Int,

    @SerializedName("quantity")
    val quantity: Int,
)

data class PaymobBillingData(
    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("street")
    val street: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("apartment")
    val apartment: String = "NA",

    @SerializedName("floor")
    val floor: String = "NA",

    @SerializedName("building")
    val building: String = "NA",

    @SerializedName("state")
    val state: String = "NA",

    @SerializedName("postal_code")
    val postalCode: String = "NA",
)
