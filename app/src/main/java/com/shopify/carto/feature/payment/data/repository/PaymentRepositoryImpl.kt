package com.shopify.carto.feature.payment.data.repository

import android.util.Log
import com.shopify.carto.BuildConfig
import com.shopify.carto.feature.payment.data.remote.PaymentRemoteDataSource
import com.shopify.carto.feature.payment.data.remote.dto.PaymobBillingData
import com.shopify.carto.feature.payment.data.remote.dto.PaymobIntentionRequest
import com.shopify.carto.feature.payment.data.remote.dto.PaymobItem
import com.shopify.carto.feature.payment.domain.model.PaymentError
import com.shopify.carto.feature.payment.domain.model.PaymentMethod
import com.shopify.carto.feature.payment.domain.model.PaymentRequest
import com.shopify.carto.feature.payment.domain.model.PaymentResult
import com.shopify.carto.feature.payment.domain.repository.PaymentRepository
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.UUID
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val remoteDataSource: PaymentRemoteDataSource,
) : PaymentRepository {

    override suspend fun createPaymentIntention(request: PaymentRequest): PaymentResult {
        return try {
            val integrationId = when (request.paymentMethod) {
                PaymentMethod.DIGITAL_WALLET ->
                    BuildConfig.PAYMOB_WALLET_INTEGRATION_ID.toIntOrNull() ?: 0
                else ->
                    BuildConfig.PAYMOB_INTEGRATION_ID.toIntOrNull() ?: 0
            }

            val apiRequest = PaymobIntentionRequest(
                amount = request.amountCents,
                currency = request.currency,
                paymentMethods = listOf(integrationId),
                items = request.items.map { item ->
                    PaymobItem(
                        name = item.name,
                        amount = item.amountCents,
                        quantity = item.quantity,
                    )
                },
                billingData = PaymobBillingData(
                    firstName = request.customerFirstName,
                    lastName = request.customerLastName,
                    email = request.customerEmail,
                    phoneNumber = request.customerPhone,
                    street = request.address,
                    city = request.city,
                    country = request.country,
                ),
            )

            val response = remoteDataSource.createPaymentIntention(apiRequest)

            val clientSecret = response.clientSecret
            if (clientSecret.isNullOrBlank()) {
                Log.e(TAG, "Paymob returned null/blank client_secret")
                PaymentResult.Failure(PaymentError.INVALID_RESPONSE)
            } else {
                PaymentResult.Success(
                    clientSecret = clientSecret,
                    orderId = response.intentionId.orEmpty(),
                )
            }
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Payment intention timeout", e)
            PaymentResult.Failure(PaymentError.TIMEOUT)
        } catch (e: IOException) {
            Log.e(TAG, "Payment intention network error", e)
            val error = if (e.message?.contains("Paymob API error") == true) {
                PaymentError.SERVER_ERROR
            } else {
                PaymentError.NETWORK
            }
            PaymentResult.Failure(error, e.message ?: error.userMessage)
        } catch (e: Exception) {
            Log.e(TAG, "Payment intention unexpected error", e)
            PaymentResult.Failure(PaymentError.UNKNOWN)
        }
    }

    override suspend fun placeCashOnDeliveryOrder(request: PaymentRequest): PaymentResult {
        return try {
            // TODO: Create a Shopify draft order here when Shopify integration is ready.
            // For now, generate a local order ID to simulate order placement.
            val localOrderId = "COD-${UUID.randomUUID().toString().take(8).uppercase()}"

            Log.d(TAG, "COD order placed: $localOrderId, amount: ${request.amountCents} ${request.currency}")

            PaymentResult.Success(
                orderId = localOrderId,
                transactionId = localOrderId,
            )
        } catch (e: Exception) {
            Log.e(TAG, "COD order placement failed", e)
            PaymentResult.Failure(PaymentError.UNKNOWN)
        }
    }

    override suspend fun checkPaymentStatus(clientSecret: String): Boolean {
        return try {
            val response = remoteDataSource.getPaymentIntention(clientSecret)
            Log.d(TAG, "checkPaymentStatus response: $response")
            val status = response.status?.lowercase()
            response.confirmed == true || 
                    status == "confirmed" || 
                    status == "paid" || 
                    status == "completed" || 
                    status == "captured"
        } catch (e: Exception) {
            Log.e(TAG, "Error checking payment status for clientSecret: $clientSecret", e)
            false
        }
    }

    companion object {
        private const val TAG = "PaymentRepository"
    }
}
