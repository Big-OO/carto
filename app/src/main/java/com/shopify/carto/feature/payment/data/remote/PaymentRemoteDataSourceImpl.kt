package com.shopify.carto.feature.payment.data.remote

import com.shopify.carto.feature.payment.data.remote.dto.PaymobIntentionRequest
import com.shopify.carto.feature.payment.data.remote.dto.PaymobIntentionResponse
import java.io.IOException
import javax.inject.Inject

class PaymentRemoteDataSourceImpl @Inject constructor(
    private val paymentApiService: PaymentApiService,
) : PaymentRemoteDataSource {

    override suspend fun createPaymentIntention(
        request: PaymobIntentionRequest,
    ): PaymobIntentionResponse {
        val response = paymentApiService.createPayment(request)

        if (response.isSuccessful) {
            return response.body()
                ?: throw IOException("Empty response body from Supabase API")
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            throw IOException(
                "Supabase API error ${response.code()}: $errorBody"
            )
        }
    }

    override suspend fun getPaymentIntention(
        clientSecret: String,
    ): PaymobIntentionResponse {
        val response = paymentApiService.getPaymentIntention(clientSecret)

        if (response.isSuccessful) {
            return response.body()
                ?: throw IOException("Empty response body from Supabase API")
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            throw IOException(
                "Supabase API error ${response.code()}: $errorBody"
            )
        }
    }
}
