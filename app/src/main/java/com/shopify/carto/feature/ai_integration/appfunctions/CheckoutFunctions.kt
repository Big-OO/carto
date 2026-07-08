package com.shopify.carto.feature.ai_integration.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.shopify.carto.core.utils.PhoneNormalizer
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
import com.shopify.carto.feature.settings.domain.repository.SettingsRepository
import com.shopify.carto.feature.currency.domain.repository.CurrencyRepository
import com.shopify.carto.feature.currency.domain.model.Currency as AppCurrency
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class CheckoutFunctions @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val observeAppSessionUseCase: ObserveAppSessionUseCase,
    private val observeProfileUseCase: ObserveProfileUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val placeCashOnDeliveryOrderUseCase: PlaceCashOnDeliveryOrderUseCase,
    private val createCardPaymentUseCase: CreateCardPaymentUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val settingsRepository: SettingsRepository,
    private val currencyRepository: CurrencyRepository
) {

    // ── Step 1: Customer Info ────────────────────────────────────────────

    /**
     * Retrieve the current customer's profile information.
     *
     * Returns the customer's first name, last name, email, and phone number.
     * If any fields are missing, the response indicates which ones are not available.
     * The AI should use this to verify customer information before placing an order.
     *
     * @return A formatted string with customer profile details, or a message listing missing fields.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getCustomerInfo(
        appFunctionContext: AppFunctionContext
    ): String {
        var firstName = ""
        var lastName = ""
        var email = ""
        var phone = ""

        try {
            val session = observeAppSessionUseCase().first()
            val customerIdLong = session.customerId?.toLongOrNull()
            if (customerIdLong != null) {
                val profile = observeProfileUseCase(customerIdLong).first()
                if (profile != null) {
                    firstName = profile.firstName.orEmpty()
                    lastName = profile.lastName.orEmpty()
                    email = profile.email
                    phone = PhoneNormalizer.normalize(profile.phone.orEmpty())
                }
            }
        } catch (_: Exception) {
            return "Failed to retrieve customer profile. Please ask the user to provide their name and email."
        }

        val missing = mutableListOf<String>()
        if (firstName.isBlank()) missing.add("firstName")
        if (lastName.isBlank()) missing.add("lastName")
        if (email.isBlank()) missing.add("email")

        return buildString {
            append("Customer Information:\n")
            append("- First Name: ${firstName.ifBlank { "(not set)" }}\n")
            append("- Last Name: ${lastName.ifBlank { "(not set)" }}\n")
            append("- Email: ${email.ifBlank { "(not set)" }}\n")
            append("- Phone: ${phone.ifBlank { "(not set)" }}\n")
            if (missing.isNotEmpty()) {
                append("\nMISSING_FIELDS: ${missing.joinToString(", ")}")
            }
        }
    }

    // ── Step 2: Shipping Addresses ──────────────────────────────────────

    /**
     * Retrieve all saved shipping addresses for the current customer.
     *
     * Returns a numbered list of all saved addresses with their IDs, so the user can
     * choose which address to use for the order. Each address includes street, city,
     * province, country, zip, phone, and whether it is the default address.
     * If no addresses are saved, the response indicates that the user must provide a new address.
     *
     * @return A formatted list of saved shipping addresses, or a message indicating none exist.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getShippingAddresses(
        appFunctionContext: AppFunctionContext
    ): String {
        return try {
            when (val result = getAddressesUseCase()) {
                is AddressResult.Success -> {
                    if (result.data.isEmpty()) {
                        "NO_ADDRESSES: No saved shipping addresses found. Please ask the user to provide a shipping address (street address, city, country)."
                    } else {
                        buildString {
                            append("Saved Shipping Addresses:\n\n")
                            result.data.forEachIndexed { index, addr ->
                                append("${index + 1}. ")
                                if (addr.isDefault) append("[DEFAULT] ")
                                append("(ID: ${addr.id})\n")
                                append("   Name: ${addr.firstName} ${addr.lastName}\n")
                                append("   Address: ${addr.address1}")
                                if (addr.address2.isNotBlank()) append(", ${addr.address2}")
                                append("\n")
                                append("   City: ${addr.city}\n")
                                if (addr.province.isNotBlank()) append("   Province: ${addr.province}\n")
                                append("   Country: ${addr.country}\n")
                                if (addr.zip.isNotBlank()) append("   ZIP: ${addr.zip}\n")
                                if (addr.phone.isNotBlank()) append("   Phone: ${addr.phone}\n")
                                append("\n")
                            }
                            append("Ask the user to choose an address by number or ID.")
                        }
                    }
                }
                is AddressResult.Failure -> {
                    "Failed to retrieve shipping addresses. Please ask the user to provide a shipping address manually."
                }
            }
        } catch (_: Exception) {
            "Failed to retrieve shipping addresses. Please ask the user to provide a shipping address manually."
        }
    }

    // ── Step 3: Phone Validation ────────────────────────────────────────

    /**
     * Validate and normalize a phone number.
     *
     * Takes a raw phone number string and normalizes it to the international format
     * required by the application. Returns whether the phone number is valid or invalid.
     * Use this to verify a phone number before placing an order.
     *
     * @param phone The phone number to validate.
     * @return A message indicating whether the phone is valid (with normalized form) or invalid.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun validatePhone(
        appFunctionContext: AppFunctionContext,
        phone: String
    ): String {
        if (phone.isBlank()) {
            return "INVALID: No phone number provided. Please ask the user to enter a phone number."
        }
        val normalized = PhoneNormalizer.normalize(phone)
        return if (normalized.isNotBlank() && normalized.length >= 10) {
            "VALID: Phone number normalized to $normalized"
        } else {
            "INVALID: The phone number '$phone' could not be validated. Please ask the user to enter a valid phone number."
        }
    }

    // ── Step 4/5: Order Summary ─────────────────────────────────────────

    /**
     * Generate a complete order summary without placing the order.
     *
     * Builds a detailed summary including all cart items, quantities, prices, shipping address,
     * customer information, phone number, payment method, and total. This should be shown to
     * the user for review before confirming the order.
     *
     * @param addressId The ID of the selected shipping address. Pass 0 if using manual address/city overrides.
     * @param phone The validated phone number to use for the order.
     * @param paymentMethod The payment method: 'CASH_ON_DELIVERY', 'CARD', or 'DIGITAL_WALLET'.
     * @param firstName Optional first name override (only if not from profile).
     * @param lastName Optional last name override (only if not from profile).
     * @param email Optional email override (only if not from profile).
     * @param address Optional manual street address (only if no saved address selected).
     * @param city Optional manual city (only if no saved address selected).
     * @return A formatted order summary string for user review.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getOrderSummary(
        appFunctionContext: AppFunctionContext,
        addressId: Long,
        phone: String,
        paymentMethod: String,
        firstName: String,
        lastName: String,
        email: String,
        address: String,
        city: String
    ): String {
        // Fetch cart
        val cartResult = observeCartUseCase().first()
        if (cartResult.isFailure) {
            return "Failed to retrieve your cart."
        }
        val cart = cartResult.getOrThrow()
        if (cart.isEmpty) {
            return "Your cart is empty. Please add items before checking out."
        }

        // Resolve customer info
        val resolvedInfo = resolveCustomerInfo(firstName, lastName, email)

        // Resolve address
        val resolvedAddress = if (addressId > 0) {
            resolveAddressById(addressId)
        } else {
            null
        }
        val finalAddress = address.takeIf { it.isNotBlank() }
            ?: resolvedAddress?.let {
                buildString {
                    append(it.address1)
                    if (it.address2.isNotBlank()) append(", ${it.address2}")
                }
            } ?: "(not set)"
        val finalCity = city.takeIf { it.isNotBlank() }
            ?: resolvedAddress?.city ?: "(not set)"

        val normalizedPhone = PhoneNormalizer.normalize(phone)

        val paymentLabel = when (paymentMethod.uppercase()) {
            "CARD" -> "Card"
            "DIGITAL_WALLET" -> "Wallet"
            else -> "Cash on Delivery"
        }



        val activeCurrency = settingsRepository.currency.first()
        val rates = currencyRepository.observeRates().first()
        val rate = rates?.rates?.get(activeCurrency) ?: 1.0
        val displayCurrency = activeCurrency.name

        val shippingFeeUsd = 80.0
        val convertedShippingFee = shippingFeeUsd * rate
        val convertedSubtotal = cart.subtotal * rate
        val convertedFinalTotal = (cart.subtotal + shippingFeeUsd) * rate

        // Build summary
        return buildString {
            append("ORDER SUMMARY\n")
            append("═══════════════════════════════\n\n")

            append("Products:\n")
            cart.lines.forEach { line ->
                val convertedPrice = line.price * rate
                append("  • ${line.productTitle} x${line.quantity} — ${String.format("%.2f", convertedPrice)} $displayCurrency\n")
            }
            append("\n")

            append("Customer: ${resolvedInfo.first} ${resolvedInfo.second}\n")
            append("Email: ${resolvedInfo.third}\n")
            append("Phone: $normalizedPhone\n\n")

            append("Shipping Address: $finalAddress\n")
            append("City: $finalCity\n")
            if (resolvedAddress != null) {
                if (resolvedAddress.province.isNotBlank()) append("Province: ${resolvedAddress.province}\n")
                append("Country: ${resolvedAddress.country}\n")
            }
            append("\n")

            append("Payment Method: $paymentLabel\n\n")

            append("Subtotal: ${String.format("%.2f", convertedSubtotal)} $displayCurrency\n")
            append("Shipping Fee: ${String.format("%.2f", convertedShippingFee)} $displayCurrency\n")
            append("Discounts: 0.00 $displayCurrency\n")
            append("Taxes: 0.00 $displayCurrency\n")
            append("Final Total: ${String.format("%.2f", convertedFinalTotal)} $displayCurrency\n")
        }
    }

    private suspend fun resolveCustomerInfo(
        overrideFirst: String,
        overrideLast: String,
        overrideEmail: String
    ): Triple<String, String, String> {
        var profileFirst = ""
        var profileLast = ""
        var profileEmail = ""
        try {
            val session = observeAppSessionUseCase().first()
            val customerIdLong = session.customerId?.toLongOrNull()
            if (customerIdLong != null) {
                val profile = observeProfileUseCase(customerIdLong).first()
                if (profile != null) {
                    profileFirst = profile.firstName.orEmpty()
                    profileLast = profile.lastName.orEmpty()
                    profileEmail = profile.email
                }
            }
        } catch (_: Exception) { }
        return Triple(
            overrideFirst.takeIf { it.isNotBlank() } ?: profileFirst.ifBlank { "(not set)" },
            overrideLast.takeIf { it.isNotBlank() } ?: profileLast.ifBlank { "(not set)" },
            overrideEmail.takeIf { it.isNotBlank() } ?: profileEmail.ifBlank { "(not set)" }
        )
    }

    private suspend fun resolveAddressById(addressId: Long): CustomerAddress? {
        return try {
            when (val result = getAddressesUseCase()) {
                is AddressResult.Success -> result.data.firstOrNull { it.id == addressId }
                is AddressResult.Failure -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    // ── Checkout (Place Order) ──────────────────────────────────────────

    /**
     * Place an order after all information has been verified and the user has confirmed.
     *
     * This function should ONLY be called after the AI has completed the full order flow:
     * 1. Verified customer info via getCustomerInfo
     * 2. Selected a shipping address via getShippingAddresses
     * 3. Validated the phone number via validatePhone
     * 4. Chosen a payment method
     * 5. For COD: shown order summary via getOrderSummary and received explicit user confirmation
     *
     * The confirmed parameter MUST be true to actually place the order.
     * If confirmed is false, the function returns a reminder to get user confirmation first.
     *
     * @param paymentMethod The payment method: 'CASH_ON_DELIVERY', 'CARD', or 'DIGITAL_WALLET'.
     * @param confirmed Must be true to place the order. Set to true only after the user explicitly confirms.
     * @param addressId The ID of the selected shipping address. Pass 0 to use manual address/city.
     * @param firstName The customer's first name.
     * @param lastName The customer's last name.
     * @param email The customer's email.
     * @param phone The validated phone number.
     * @param address Manual street address (used only if addressId is 0).
     * @param city Manual city (used only if addressId is 0).
     * @return A status message indicating if the order was placed successfully or failed.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun checkout(
        appFunctionContext: AppFunctionContext,
        paymentMethod: String,
        confirmed: Boolean,
        addressId: Long,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        address: String,
        city: String
    ): String {
        // Guard: must be explicitly confirmed
        if (!confirmed) {
            return "ORDER_NOT_CONFIRMED: The order has not been confirmed by the user. Please show the order summary and ask for explicit confirmation before placing the order."
        }

        // 1. Validate cart
        val cartResult = observeCartUseCase().first()
        if (cartResult.isFailure) {
            return "Failed to retrieve your cart for checkout."
        }
        val cart = cartResult.getOrThrow()
        if (cart.isEmpty) {
            return "Your cart is empty. Please add items to your cart before checking out."
        }

        // 2. Resolve customer info from profile + overrides
        val resolvedInfo = resolveCustomerInfo(firstName, lastName, email)
        val resolvedFirstName = resolvedInfo.first.takeIf { it != "(not set)" }
        val resolvedLastName = resolvedInfo.second.takeIf { it != "(not set)" }
        val resolvedEmail = resolvedInfo.third.takeIf { it != "(not set)" }

        // 3. Resolve address: by ID or manual override
        val selectedAddress = if (addressId > 0) resolveAddressById(addressId) else null
        val resolvedAddress = address.takeIf { it.isNotBlank() }
            ?: selectedAddress?.let {
                buildString {
                    append(it.address1)
                    if (it.address2.isNotBlank()) append(", ${it.address2}")
                }
            }?.takeIf { it.isNotBlank() }
        val resolvedCity = city.takeIf { it.isNotBlank() }
            ?: selectedAddress?.city?.takeIf { it.isNotBlank() }

        // 4. Resolve phone
        val resolvedPhone = phone.takeIf { it.isNotBlank() }?.let { PhoneNormalizer.normalize(it) }

        // 5. Check for missing required fields
        val missingFields = mutableListOf<String>()
        if (resolvedFirstName == null) missingFields.add("firstName")
        if (resolvedLastName == null) missingFields.add("lastName")
        if (resolvedEmail == null) missingFields.add("email")
        if (resolvedPhone == null) missingFields.add("phone")
        if (resolvedAddress == null) missingFields.add("address")
        if (resolvedCity == null) missingFields.add("city")

        if (missingFields.isNotEmpty()) {
            return "MISSING_INFO: Cannot place the order. Missing: ${missingFields.joinToString(", ")}."
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
