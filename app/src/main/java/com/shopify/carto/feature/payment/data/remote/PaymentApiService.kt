package com.shopify.carto.feature.payment.data.remote

import com.shopify.carto.feature.payment.data.remote.dto.PaymobIntentionRequest
import com.shopify.carto.feature.payment.data.remote.dto.PaymobIntentionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PaymentApiService {

    @POST("create-payment")
    suspend fun createPayment(
        @Body request: PaymobIntentionRequest
    ): Response<PaymobIntentionResponse>

    @GET("create-payment")
    suspend fun getPaymentIntention(
        @Query("client_secret") clientSecret: String
    ): Response<PaymobIntentionResponse>
}
