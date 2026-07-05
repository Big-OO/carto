package com.shopify.carto.feature.payment.data.remote

import com.shopify.carto.feature.payment.data.remote.dto.PaymobIntentionRequest
import com.shopify.carto.feature.payment.data.remote.dto.PaymobIntentionResponse

interface PaymentRemoteDataSource {

    suspend fun createPaymentIntention(
        request: PaymobIntentionRequest,
    ): PaymobIntentionResponse

    suspend fun getPaymentIntention(
        clientSecret: String,
    ): PaymobIntentionResponse
}
