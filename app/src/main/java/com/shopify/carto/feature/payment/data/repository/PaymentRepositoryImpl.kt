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
import com.shopify.carto.core.network.config.ShopifyConfig
import com.shopify.carto.feature.addresses.data.remote.CustomerIdDataSource
import com.shopify.carto.feature.addresses.data.result.AddressDataResult
import com.shopify.carto.feature.payment.data.remote.OrderRemoteDataSource
import com.shopify.carto.feature.payment.data.remote.CreateOrderGraphQlRequest
import com.shopify.carto.feature.payment.data.remote.CreateOrderVariables
import com.shopify.carto.feature.payment.data.remote.CustomerAssociateInput
import com.shopify.carto.feature.payment.data.remote.CustomerIdInput
import com.shopify.carto.feature.payment.data.remote.DiscountCodeInput
import com.shopify.carto.feature.payment.data.remote.ItemFixedDiscountInput
import com.shopify.carto.feature.payment.data.remote.LineItemInput
import com.shopify.carto.feature.payment.data.remote.MoneyInput
import com.shopify.carto.feature.payment.data.remote.OrderInput
import com.shopify.carto.feature.payment.data.remote.OrderOptionsInput
import com.shopify.carto.feature.payment.data.remote.PriceSetInput
import com.shopify.carto.feature.payment.data.remote.ShippingAddressInput
import com.shopify.carto.feature.payment.data.remote.ShippingLineInput
import com.shopify.carto.feature.payment.data.remote.TransactionInput
import com.shopify.carto.feature.payment.domain.model.PromoCodeResult
import com.shopify.carto.feature.shopping_cart.domain.repository.CartRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val remoteDataSource: PaymentRemoteDataSource,
    private val orderRemoteDataSource: OrderRemoteDataSource,
    private val cartRepository: CartRepository,
    private val customerIdDataSource: CustomerIdDataSource,
    private val shopifyConfig: ShopifyConfig,
) : PaymentRepository {

    override suspend fun createPaymentIntention(request: PaymentRequest): PaymentResult {
        return try {
            val integrationId = when (request.paymentMethod) {
                PaymentMethod.DIGITAL_WALLET ->
                    BuildConfig.PAYMOB_WALLET_INTEGRATION_ID.toIntOrNull() ?: 0
                else ->
                    BuildConfig.PAYMOB_INTEGRATION_ID.toIntOrNull() ?: 0
            }

            val paymobItems = request.items.map { item ->
                PaymobItem(
                    name = item.name,
                    amount = item.amountCents,
                    quantity = item.quantity,
                )
            }.toMutableList()

            val itemsSum = paymobItems.sumOf { it.amount * it.quantity }
            val diff = request.amountCents - itemsSum
            if (diff > 0) {
                paymobItems.add(
                    PaymobItem(
                        name = "Shipping & Fees",
                        amount = diff,
                        quantity = 1,
                    )
                )
            } else if (diff < 0 && paymobItems.isNotEmpty()) {
                var remDiscount = -diff
                for (i in 0 until paymobItems.size) {
                    if (remDiscount <= 0) break
                    val item = paymobItems[i]
                    val totalItemPrice = item.amount * item.quantity
                    if (totalItemPrice > remDiscount + item.quantity) {
                        val newTotal = totalItemPrice - remDiscount
                        val newAmount = newTotal / item.quantity
                        val remainder = newTotal % item.quantity
                        paymobItems[i] = item.copy(amount = newAmount)
                        remDiscount = remainder
                    } else {
                        val canDeduct = totalItemPrice - item.quantity
                        paymobItems[i] = item.copy(amount = 1)
                        remDiscount -= canDeduct
                    }
                }
                val newSum = paymobItems.sumOf { it.amount * it.quantity }
                val finalDiff = request.amountCents - newSum
                if (finalDiff > 0) {
                    paymobItems.add(PaymobItem("Adjustment", finalDiff, 1))
                }
            }

            val apiRequest = PaymobIntentionRequest(
                amount = request.amountCents,
                currency = request.currency,
                paymentMethods = listOf(integrationId),
                items = paymobItems,
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
        return createShopifyOrder(request, financialStatus = "PENDING")
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

    override suspend fun createShopifyOrder(request: PaymentRequest, financialStatus: String): PaymentResult {
        return try {
            val customerIdResult = customerIdDataSource.getShopifyCustomerId()
            val customerNumericId = if (customerIdResult is AddressDataResult.Success) {
                customerIdResult.data
            } else null

            val customerAssociate = customerNumericId?.let {
                CustomerAssociateInput(CustomerIdInput("gid://shopify/Customer/$it"))
            }

            val lineItems = if (request.items.isNotEmpty()) {
                request.items.map { item ->
                    val formattedPrice = String.format(java.util.Locale.US, "%.2f", item.amountCents / 100.0)
                    val money = MoneyInput(amount = formattedPrice, currencyCode = request.currency)
                    LineItemInput(
                        variantId = item.variantId.ifBlank { "gid://shopify/ProductVariant/42279717404727" },
                        quantity = item.quantity,
                        priceSet = PriceSetInput(
                            shopMoney = money,
                            presentmentMoney = money,
                        )
                    )
                }
            } else {
                return PaymentResult.Failure(PaymentError.VALIDATION_FAILED, "Cart is empty")
            }

            val shippingAddr = ShippingAddressInput(
                firstName = request.customerFirstName.ifBlank { "Customer" },
                lastName = request.customerLastName.ifBlank { "Name" },
                address1 = request.address.ifBlank { "Address" },
                city = request.city.ifBlank { "City" },
                provinceCode = "NY",
                countryCode = request.country.ifBlank { "US" },
                zip = "10001",
                phone = request.customerPhone
            )

            val shippingMoney = MoneyInput(amount = "80.00", currencyCode = request.currency)
            val shippingLines = listOf(
                ShippingLineInput(
                    title = "Standard Shipping",
                    code = "Standard",
                    source = "shopify",
                    priceSet = PriceSetInput(
                        shopMoney = shippingMoney,
                        presentmentMoney = shippingMoney,
                    )
                )
            )

            val discountInput = if (!request.discountCode.isNullOrBlank() && request.discountAmountCents > 0) {
                val discountMoney = MoneyInput(
                    amount = String.format(java.util.Locale.US, "%.2f", request.discountAmountCents / 100.0),
                    currencyCode = request.currency
                )
                DiscountCodeInput(
                    itemFixedDiscountCode = ItemFixedDiscountInput(
                        code = request.discountCode,
                        amountSet = PriceSetInput(
                            shopMoney = discountMoney,
                            presentmentMoney = discountMoney,
                        )
                    )
                )
            } else null

            val transactions = if (financialStatus.equals("PAID", ignoreCase = true)) {
                val totalMoney = MoneyInput(
                    amount = String.format(java.util.Locale.US, "%.2f", request.amountCents / 100.0),
                    currencyCode = request.currency
                )
                listOf(
                    TransactionInput(
                        kind = "SALE",
                        status = "SUCCESS",
                        gateway = "manual",
                        amountSet = PriceSetInput(
                            shopMoney = totalMoney,
                            presentmentMoney = totalMoney,
                        )
                    )
                )
            } else {
                emptyList()
            }

            val orderInput = OrderInput(
                currency = request.currency,
                email = request.customerEmail.ifBlank { "customer@example.com" },
                phone = request.customerPhone.takeIf { it.isNotBlank() },
                customer = customerAssociate,
                lineItems = lineItems,
                shippingAddress = shippingAddr,
                shippingLines = shippingLines,
                discountCode = discountInput,
                financialStatus = financialStatus.uppercase(),
                transactions = transactions
            )

            val query = """
                mutation CreateOrder(${"$"}order: OrderCreateOrderInput!, ${"$"}options: OrderCreateOptionsInput) {
                  orderCreate(order: ${"$"}order, options: ${"$"}options) {
                    order {
                      id
                      name
                    }
                    userErrors {
                      field
                      message
                    }
                  }
                }
            """.trimIndent()

            val graphQlRequest = CreateOrderGraphQlRequest(
                query = query,
                variables = CreateOrderVariables(
                    order = orderInput,
                    options = OrderOptionsInput(
                        inventoryBehaviour = "DECREMENT_OBEYING_POLICY",
                        sendReceipt = true,
                        sendFulfillmentReceipt = false
                    )
                )
            )

            val result = orderRemoteDataSource.createOrder(shopifyConfig.apiVersion, graphQlRequest)
            if (result.isSuccess) {
                val order = result.getOrNull()
                val orderId = order?.id ?: "ORDER-${UUID.randomUUID().toString().take(8)}"
                val transactionId = order?.name ?: orderId
                cartRepository.clearCart()
                PaymentResult.Success(orderId = orderId, transactionId = transactionId)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to create order on Shopify"
                PaymentResult.Failure(PaymentError.SERVER_ERROR, errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "createShopifyOrder error", e)
            PaymentResult.Failure(PaymentError.UNKNOWN, e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun applyPromoCode(code: String, subtotalCents: Int): PromoCodeResult {
        return try {
            val rulesResult = orderRemoteDataSource.getPriceRules(shopifyConfig.apiVersion)
            if (rulesResult.isSuccess) {
                val rules = rulesResult.getOrNull() ?: emptyList()
                val matchingRule = rules.find { it.title?.equals(code, ignoreCase = true) == true }
                if (matchingRule != null) {
                    val valStr = matchingRule.value ?: "0.0"
                    val numVal = kotlin.math.abs(valStr.toDoubleOrNull() ?: 0.0)
                    val discountCents = if (matchingRule.value_type == "percentage") {
                        (subtotalCents * (numVal / 100.0)).toInt()
                    } else {
                        (numVal * 100).toInt()
                    }
                    return PromoCodeResult(
                        isValid = true,
                        code = matchingRule.title ?: code,
                        discountAmountCents = kotlin.math.min(discountCents, subtotalCents)
                    )
                }
            }

            val lookupResult = orderRemoteDataSource.lookupDiscountCode(shopifyConfig.apiVersion, code)
            if (lookupResult.isSuccess) {
                val discountCodeDto = lookupResult.getOrNull()
                if (discountCodeDto != null && discountCodeDto.price_rule_id != null) {
                    val allRules = orderRemoteDataSource.getPriceRules(shopifyConfig.apiVersion).getOrNull() ?: emptyList()
                    val rule = allRules.find { it.id == discountCodeDto.price_rule_id }
                    if (rule != null) {
                        val valStr = rule.value ?: "0.0"
                        val numVal = kotlin.math.abs(valStr.toDoubleOrNull() ?: 0.0)
                        val discountCents = if (rule.value_type == "percentage") {
                            (subtotalCents * (numVal / 100.0)).toInt()
                        } else {
                            (numVal * 100).toInt()
                        }
                        return PromoCodeResult(
                            isValid = true,
                            code = discountCodeDto.code ?: code,
                            discountAmountCents = kotlin.math.min(discountCents, subtotalCents)
                        )
                    }
                }
            }

            PromoCodeResult(isValid = false, errorMessage = "Invalid or expired discount code")
        } catch (e: Exception) {
            Log.e(TAG, "applyPromoCode error", e)
            PromoCodeResult(isValid = false, errorMessage = "Error verifying promo code")
        }
    }

    companion object {
        private const val TAG = "PaymentRepository"
    }
}
