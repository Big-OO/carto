package com.shopify.carto.feature.payment.data.remote

import com.shopify.carto.feature.payment.data.remote.dto.ConfirmPaymentRequest
import com.shopify.carto.feature.payment.data.remote.dto.ConfirmPaymentResponse
import com.shopify.carto.feature.payment.data.remote.dto.PaymentMethodsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymobFlashApiService {

    @GET("v1/intention/element/{publicKey}/{clientSecret}/")
    suspend fun getAvailablePaymentMethods(
        @Path("publicKey") publicKey: String,
        @Path("clientSecret") clientSecret: String,
    ): Response<PaymentMethodsResponse>

    @POST("v1/intention/confirm/customer/")
    suspend fun confirmPayment(
        @Body request: ConfirmPaymentRequest,
    ): Response<ConfirmPaymentResponse>
}
