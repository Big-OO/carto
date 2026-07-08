package com.shopify.carto.feature.ai_integration.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.shopify.carto.feature.addresses.domain.model.AddressResult
import com.shopify.carto.feature.addresses.domain.model.CustomerAddress
import com.shopify.carto.feature.addresses.domain.usecase.GetAddressesUseCase
import com.shopify.carto.feature.profile.domain.usecase.ObserveProfileUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.ObserveCartUseCase
import com.shopify.carto.feature.payment.domain.usecase.PlaceCashOnDeliveryOrderUseCase
import com.shopify.carto.feature.payment.domain.usecase.CreateCardPaymentUseCase
import com.shopify.carto.feature.orderdetails.domain.usecase.CancelOrderUseCase
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsResult
import com.shopify.carto.feature.payment.domain.model.PaymentRequest
import com.shopify.carto.feature.payment.domain.model.PaymentMethod
import com.shopify.carto.feature.payment.domain.model.PaymentResult
import com.shopify.carto.feature.payment.domain.model.OrderItem
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class CheckoutFunctions @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val observeAppSessionUseCase: ObserveAppSessionUseCase,
    private val observeProfileUseCase: ObserveProfileUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val placeCashOnDeliveryOrderUseCase: PlaceCashOnDeliveryOrderUseCase,
    private val createCardPaymentUseCase: CreateCardPaymentUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase
) {

    /**
     * Checkout the current cart and place an order.
     *
     * This function automatically fetches the user's profile (full name, email, phone) and
     * default shipping address. The AI should call this function with only the paymentMethod
     * parameter. If any required information (firstName, lastName, email, phone, address, city)
     * cannot be resolved from the profile or default address, the function returns a message
     * listing the missing fields so the AI can ask the user.
     *
     * @param paymentMethod The payment method to use (either "CASH_ON_DELIVERY", "CARD", or "DIGITAL_WALLET"). Defaults to "CASH_ON_DELIVERY".
     * @param firstName Optional first name override. Auto-fetched from profile if empty.
     * @param lastName Optional last name override. Auto-fetched from profile if empty.
     * @param email Optional email override. Auto-fetched from profile if empty.
     * @param phone Optional phone override. Auto-fetched from profile or default address if empty.
     * @param address Optional address override. Auto-fetched from default address if empty.
     * @param city Optional city override. Auto-fetched from default address if empty.
     * @return A status message indicating if checkout succeeded, failed, or requires the user to provide missing info.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun checkout(
        appFunctionContext: AppFunctionContext,
        paymentMethod: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        address: String,
        city: String
    ): String {
        // 1. Validate cart
        val cartResult = observeCartUseCase().first()
        if (cartResult.isFailure) {
            return "Failed to retrieve your cart for checkout."
        }
        val cart = cartResult.getOrThrow()
        if (cart.isEmpty) {
            return "Your cart is empty. Please add items to your cart before checking out."
        }

        // 2. Fetch profile data (name, email, phone)
        var profileFirstName = ""
        var profileLastName = ""
        var profileEmail = ""
        var profilePhone = ""

        try {
            val session = observeAppSessionUseCase().first()
            val customerIdLong = session.customerId?.toLongOrNull()
            if (customerIdLong != null) {
                val profile = observeProfileUseCase(customerIdLong).first()
                if (profile != null) {
                    profileFirstName = profile.firstName.orEmpty()
                    profileLastName = profile.lastName.orEmpty()
                    profileEmail = profile.email
                    profilePhone = profile.phone.orEmpty()
                }
            }
        } catch (_: Exception) {
            // Profile fetch failed, will rely on overrides or ask user
        }

        // 3. Fetch default address
        var defaultAddress: CustomerAddress? = null
        try {
            when (val addressResult = getAddressesUseCase()) {
                is AddressResult.Success -> {
                    defaultAddress = addressResult.data.firstOrNull { it.isDefault }
                        ?: addressResult.data.firstOrNull()
                }
                is AddressResult.Failure -> {
                    // Address fetch failed, will rely on overrides or ask user
                }
            }
        } catch (_: Exception) {
            // Address fetch failed
        }

        // 4. Resolve fields: override > profile/address > null
        val resolvedFirstName = firstName.takeIf { it.isNotBlank() }
            ?: profileFirstName.takeIf { it.isNotBlank() }
            ?: defaultAddress?.firstName?.takeIf { it.isNotBlank() }
        val resolvedLastName = lastName.takeIf { it.isNotBlank() }
            ?: profileLastName.takeIf { it.isNotBlank() }
            ?: defaultAddress?.lastName?.takeIf { it.isNotBlank() }
        val resolvedEmail = email.takeIf { it.isNotBlank() }
            ?: profileEmail.takeIf { it.isNotBlank() }
        val resolvedPhone = phone.takeIf { it.isNotBlank() }
            ?: profilePhone.takeIf { it.isNotBlank() }
            ?: defaultAddress?.phone?.takeIf { it.isNotBlank() }
        val resolvedAddress = address.takeIf { it.isNotBlank() }
            ?: defaultAddress?.let {
                buildString {
                    append(it.address1)
                    if (it.address2.isNotBlank()) append(", ${it.address2}")
                }
            }?.takeIf { it.isNotBlank() }
        val resolvedCity = city.takeIf { it.isNotBlank() }
            ?: defaultAddress?.city?.takeIf { it.isNotBlank() }

        // 5. Check for missing required fields
        val missingFields = mutableListOf<String>()
        if (resolvedFirstName == null) missingFields.add("firstName")
        if (resolvedLastName == null) missingFields.add("lastName")
        if (resolvedEmail == null) missingFields.add("email")
        if (resolvedPhone == null) missingFields.add("phone")
        if (resolvedAddress == null) missingFields.add("address")
        if (resolvedCity == null) missingFields.add("city")

        if (missingFields.isNotEmpty()) {
            return "MISSING_INFO: Cannot place the order because the following information is missing: ${missingFields.joinToString(", ")}. " +
                    "Please ask the user to provide: ${missingFields.joinToString(", ")}."
        }

        // 6. Build order items
        val orderItems = cart.lines.map { line ->
            OrderItem(
                name = line.productTitle,
                quantity = line.quantity,
                amountCents = (line.price * 100).toInt(),
                variantId = line.merchandiseId,
                imageUrl = line.imageUrl
            )
        }

        val resolvedMethod = when (paymentMethod.uppercase()) {
            "CARD" -> PaymentMethod.CARD
            "DIGITAL_WALLET" -> PaymentMethod.DIGITAL_WALLET
            else -> PaymentMethod.CASH_ON_DELIVERY
        }

        val amountCents = (cart.total * 100).toInt()

        val request = PaymentRequest(
            amountCents = amountCents,
            currency = cart.currency,
            paymentMethod = resolvedMethod,
            customerFirstName = resolvedFirstName!!,
            customerLastName = resolvedLastName!!,
            customerEmail = resolvedEmail!!,
            customerPhone = resolvedPhone!!,
            address = resolvedAddress!!,
            city = resolvedCity!!,
            items = orderItems
        )

        // 7. Place order
        return when (resolvedMethod) {
            PaymentMethod.CASH_ON_DELIVERY -> {
                val result = placeCashOnDeliveryOrderUseCase(request)
                when (result) {
                    is PaymentResult.Success -> {
                        "Order successfully placed with Cash on Delivery! Order ID: ${result.orderId}"
                    }
                    is PaymentResult.Failure -> {
                        "Failed to place Cash on Delivery order: ${result.message}"
                    }
                    PaymentResult.Cancelled -> {
                        "Order placement was cancelled."
                    }
                }
            }
            PaymentMethod.CARD, PaymentMethod.DIGITAL_WALLET -> {
                val result = createCardPaymentUseCase(request)
                when (result) {
                    is PaymentResult.Success -> {
                        "Payment intention created for ${paymentMethod.uppercase()}! Please complete your transaction in the checkout page using client secret: ${result.clientSecret}"
                    }
                    is PaymentResult.Failure -> {
                        "Failed to initialize ${paymentMethod.uppercase()} payment: ${result.message}"
                    }
                    PaymentResult.Cancelled -> {
                        "Payment initialization was cancelled."
                    }
                }
            }
        }
    }

    /**
     * Cancel an existing order by its ID.
     *
     * Use this when the user wants to cancel an order, terminate their purchase, or stop a transaction.
     *
     * @param orderId The unique Shopify order ID to cancel.
     * @return A status message indicating if order cancellation succeeded or failed.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun cancelOrder(
        appFunctionContext: AppFunctionContext,
        orderId: String
    ): String {
        if (orderId.isBlank()) {
            return "Please provide a valid order ID to cancel."
        }
        return when (val result = cancelOrderUseCase(orderId)) {
            is OrderDetailsResult.Success -> {
                "Order $orderId has been successfully cancelled."
            }
            is OrderDetailsResult.Failure -> {
                "Failed to cancel order: ${result.failure.message}"
            }
        }
    }
}
