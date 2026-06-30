package com.example.carto.feature.register.data.shopify.model

import com.google.gson.annotations.SerializedName

data class ShopifyCustomerBody(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String,
    @SerializedName("verified_email")
    val verifiedEmail: Boolean,
    @SerializedName("send_email_welcome")
    val sendEmailWelcome: Boolean,
    val tags: String,
    val note: String,
)
