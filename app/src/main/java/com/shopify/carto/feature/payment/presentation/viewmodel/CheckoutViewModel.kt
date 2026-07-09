package com.shopify.carto.feature.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.shopify.carto.feature.addresses.domain.model.AddressResult
import com.shopify.carto.feature.addresses.domain.usecase.GetAddressesUseCase
import com.shopify.carto.feature.payment.domain.model.OrderItem
import com.shopify.carto.feature.payment.domain.model.PaymentMethod
import com.shopify.carto.feature.payment.domain.model.PaymentRequest
import com.shopify.carto.feature.payment.domain.model.PaymentResult
import com.shopify.carto.feature.payment.domain.usecase.ApplyPromoCodeUseCase
import com.shopify.carto.feature.payment.domain.usecase.CheckPaymentStatusUseCase
import com.shopify.carto.feature.payment.domain.usecase.CreateCardPaymentUseCase
import com.shopify.carto.feature.payment.domain.usecase.PlaceCashOnDeliveryOrderUseCase
import com.shopify.carto.feature.payment.domain.usecase.PlaceShopifyOrderUseCase
import com.shopify.carto.feature.payment.domain.usecase.ValidateCheckoutUseCase
import com.shopify.carto.feature.payment.domain.usecase.ValidationResult
import com.shopify.carto.feature.payment.presentation.state.CheckoutEvent
import com.shopify.carto.feature.payment.presentation.state.CheckoutUiEvent
import com.shopify.carto.feature.payment.presentation.state.CheckoutUiState
import com.shopify.carto.feature.profile.domain.usecase.ObserveProfileUseCase
import com.shopify.carto.feature.shopping_cart.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val createCardPaymentUseCase: CreateCardPaymentUseCase,
    private val placeCashOnDeliveryOrderUseCase: PlaceCashOnDeliveryOrderUseCase,
    private val placeShopifyOrderUseCase: PlaceShopifyOrderUseCase,
    private val applyPromoCodeUseCase: ApplyPromoCodeUseCase,
    private val validateCheckoutUseCase: ValidateCheckoutUseCase,
    private val checkPaymentStatusUseCase: CheckPaymentStatusUseCase,
    private val getAddressesUseCase: GetAddressesUseCase,
    private val cartRepository: CartRepository,
    private val observeAppSessionUseCase: ObserveAppSessionUseCase,
    private val observeProfileUseCase: ObserveProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<CheckoutUiEvent>()
    val events: SharedFlow<CheckoutUiEvent> = _events.asSharedFlow()

    private var activeClientSecret: String? = null

    init {
        observeProfile()
        loadAddresses()
        observeCart()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            observeAppSessionUseCase().collect { session ->
                val customerId = session.customerId?.toLongOrNull() ?: return@collect
                observeProfileUseCase(customerId).collect { profile ->
                    if (profile != null) {
                        _state.update { current ->
                            val normalizedPhone = profile.phone?.let { com.shopify.carto.core.utils.PhoneNormalizer.normalize(it) }
                            current.copy(
                                customerEmail = profile.email.ifBlank { current.customerEmail },
                                customerFirstName = profile.firstName.ifBlank { current.customerFirstName },
                                customerLastName = profile.lastName.ifBlank { current.customerLastName },
                                customerPhone = normalizedPhone?.ifBlank { current.customerPhone } ?: current.customerPhone
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            when (val result = getAddressesUseCase()) {
                is AddressResult.Success -> {
                    val defaultAddr = result.data.firstOrNull { it.isDefault } ?: result.data.firstOrNull()
                    if (defaultAddr != null) {
                        _state.update {
                            val normalizedPhone = com.shopify.carto.core.utils.PhoneNormalizer.normalize(defaultAddr.phone)
                            val cityText = defaultAddr.city.ifBlank { it.city }
                            val addrText = defaultAddr.address1.ifBlank { it.address }
                            it.copy(
                                selectedAddress = defaultAddr,
                                customerFirstName = defaultAddr.firstName.ifBlank { it.customerFirstName },
                                customerLastName = defaultAddr.lastName.ifBlank { it.customerLastName },
                                customerPhone = normalizedPhone.ifBlank { it.customerPhone },
                                address = addrText,
                                city = cityText,
                                shippingFeeCents = calculateShippingFeeCents(cityText, addrText),
                                validationErrors = emptyMap(),
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            cartRepository.observeCart().collect { result ->
                val cart = result.getOrNull()
                if (cart != null) {
                    val items = cart.lines.map { line ->
                        OrderItem(
                            name = line.productTitle,
                            quantity = line.quantity,
                            amountCents = (line.price * 100).toInt(),
                            variantId = line.merchandiseId,
                            imageUrl = line.imageUrl,
                            variantTitle = line.variantTitle,
                        )
                    }
                    val subtotal = items.sumOf { it.amountCents * it.quantity }
                    _state.update {
                        it.copy(
                            orderItems = items,
                            subtotalAmountCents = subtotal
                        )
                    }
                }
            }
        }
    }

    suspend fun verifyPaymentStatus(clientSecret: String): Boolean {
        return checkPaymentStatusUseCase(clientSecret)
    }

    fun onEvent(event: CheckoutEvent) {
        when (event) {
            is CheckoutEvent.UpdateFirstName ->
                _state.update {
                    it.copy(
                        customerFirstName = event.value,
                        validationErrors = it.validationErrors - "firstName"
                    )
                }

            is CheckoutEvent.UpdateLastName ->
                _state.update {
                    it.copy(
                        customerLastName = event.value,
                        validationErrors = it.validationErrors - "lastName"
                    )
                }

            is CheckoutEvent.UpdateEmail ->
                _state.update {
                    it.copy(
                        customerEmail = event.value,
                        validationErrors = it.validationErrors - "email"
                    )
                }

            is CheckoutEvent.UpdatePhone ->
                _state.update {
                    it.copy(
                        customerPhone = event.value,
                        validationErrors = it.validationErrors - "phone"
                    )
                }

            is CheckoutEvent.UpdateAddress ->
                _state.update {
                    it.copy(
                        address = event.value,
                        shippingFeeCents = calculateShippingFeeCents(it.city, event.value),
                        validationErrors = it.validationErrors - "address"
                    )
                }

            is CheckoutEvent.UpdateCity ->
                _state.update {
                    it.copy(
                        city = event.value,
                        shippingFeeCents = calculateShippingFeeCents(event.value, it.address),
                        validationErrors = it.validationErrors - "city"
                    )
                }

            is CheckoutEvent.SelectPaymentMethod ->
                _state.update { it.copy(selectedPaymentMethod = event.method) }

            is CheckoutEvent.UpdatePromoCodeInput ->
                _state.update { it.copy(promoCodeInput = event.value, promoCodeError = null) }

            is CheckoutEvent.ApplyPromoCode -> applyPromoCode(event.code)

            is CheckoutEvent.RefreshCheckout -> loadAddresses()

            is CheckoutEvent.PlaceOrder -> placeOrder()
            is CheckoutEvent.RetryPayment -> retryPayment()
        }
    }

    private fun applyPromoCode(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true, promoCodeError = null) }
            val res = applyPromoCodeUseCase(code, _state.value.subtotalAmountCents)
            _state.update {
                it.copy(
                    isProcessing = false,
                    appliedPromoCode = if (res.isValid) res.code else null,
                    discountAmountCents = if (res.isValid) res.discountAmountCents else 0,
                    promoCodeError = if (!res.isValid) res.errorMessage else null,
                    promoCodeInput = if (res.isValid) "" else it.promoCodeInput
                )
            }
        }
    }

    private fun placeOrder() {
        val current = _state.value
        val normalizedPhone = com.shopify.carto.core.utils.PhoneNormalizer.normalize(current.customerPhone)
        _state.update { it.copy(customerPhone = normalizedPhone) }
        val updatedCurrent = _state.value

        val validation = validateCheckoutUseCase(
            firstName = updatedCurrent.customerFirstName,
            lastName = updatedCurrent.customerLastName,
            email = updatedCurrent.customerEmail,
            phone = updatedCurrent.customerPhone,
            address = updatedCurrent.address,
            city = updatedCurrent.city,
        )

        when (validation) {
            is ValidationResult.Invalid -> {
                _state.update { it.copy(validationErrors = validation.errors) }
                val firstError = validation.errors.values.firstOrNull() ?: "Please check your information"
                viewModelScope.launch {
                    _events.emit(CheckoutUiEvent.PaymentFailed(firstError))
                }
                return
            }

            is ValidationResult.Valid -> {
                _state.update { it.copy(validationErrors = emptyMap()) }
            }
        }

        val request = PaymentRequest(
            amountCents = updatedCurrent.totalAmountCents,
            paymentMethod = updatedCurrent.selectedPaymentMethod,
            customerFirstName = updatedCurrent.customerFirstName,
            customerLastName = updatedCurrent.customerLastName,
            customerEmail = updatedCurrent.customerEmail,
            customerPhone = updatedCurrent.customerPhone,
            address = updatedCurrent.address,
            city = updatedCurrent.city,
            items = updatedCurrent.orderItems,
            discountCode = updatedCurrent.appliedPromoCode,
            discountAmountCents = updatedCurrent.discountAmountCents,
        )

        _state.update {
            it.copy(isProcessing = true)
        }

        viewModelScope.launch {
            when (updatedCurrent.selectedPaymentMethod) {
                PaymentMethod.CASH_ON_DELIVERY -> processCodOrder(request)
                PaymentMethod.CARD -> processCardPayment(request)
                PaymentMethod.DIGITAL_WALLET -> processCardPayment(request)
            }
        }
    }

    private suspend fun processCardPayment(request: PaymentRequest) {
        when (val result = createCardPaymentUseCase(request)) {
            is PaymentResult.Success -> {
                activeClientSecret = result.clientSecret
                _state.update {
                    it.copy(isProcessing = false)
                }
                _events.emit(CheckoutUiEvent.LaunchPaymob(result.clientSecret))
            }

            is PaymentResult.Failure -> {
                _state.update {
                    it.copy(isProcessing = false)
                }
                _events.emit(CheckoutUiEvent.PaymentFailed(result.message))
            }

            is PaymentResult.Cancelled -> {
                _state.update {
                    it.copy(isProcessing = false)
                }
                return
            }
        }
    }

    private suspend fun processCodOrder(request: PaymentRequest) {
        when (val result = placeCashOnDeliveryOrderUseCase(request)) {
            is PaymentResult.Success -> {
                cartRepository.clearCart()
                _state.update {
                    it.copy(isProcessing = false)
                }
                val numericOrderId = result.orderId.substringAfterLast("/")
                _events.emit(
                    CheckoutUiEvent.PaymentSuccess(
                        transactionId = result.transactionId,
                        orderId = numericOrderId,
                    )
                )
            }

            is PaymentResult.Failure -> {
                _state.update {
                    it.copy(isProcessing = false)
                }
                _events.emit(CheckoutUiEvent.PaymentFailed(result.message))
            }

            is PaymentResult.Cancelled -> {
                _state.update {
                    it.copy(isProcessing = false)
                }
                _events.emit(CheckoutUiEvent.PaymentFailed("Order cancelled"))
            }
        }
    }

    fun onPaymobSuccess(transactionId: String) {
        activeClientSecret?.let { secret ->
            onPaymentCompleted(secret)
        } ?: run {
            onPaymentCompleted("")
        }
    }

    fun onPaymentCompleted(clientSecret: String) {
        if (clientSecret.isBlank()) {
            activeClientSecret?.let {
                onPaymentCompleted(it)
                return
            }
            viewModelScope.launch {
                _state.update { it.copy(isProcessing = false) }
                _events.emit(CheckoutUiEvent.PaymentFailed("Payment client secret was missing. Verification aborted."))
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }

            var verified = false
            for (i in 1..3) {
                try {
                    verified = verifyPaymentStatus(clientSecret)
                    if (verified) break
                } catch (e: Exception) {
                    Timber.tag(TAG).e("Payment verification retry $i failed ${e.message}")
                }
                delay(1500.milliseconds)
            }

            val current = _state.value
            val request = PaymentRequest(
                amountCents = current.totalAmountCents,
                paymentMethod = current.selectedPaymentMethod,
                customerFirstName = current.customerFirstName,
                customerLastName = current.customerLastName,
                customerEmail = current.customerEmail,
                customerPhone = current.customerPhone,
                address = current.address,
                city = current.city,
                items = current.orderItems,
                discountCode = current.appliedPromoCode,
                discountAmountCents = current.discountAmountCents,
            )

            when (val orderResult = placeShopifyOrderUseCase(request, financialStatus = "PAID")) {
                is PaymentResult.Success -> {
                    cartRepository.clearCart()
                    _state.update { it.copy(isProcessing = false) }
                    val numericOrderId = orderResult.orderId.substringAfterLast("/")
                    _events.emit(CheckoutUiEvent.PaymentSuccess(orderResult.transactionId, numericOrderId))
                }
                is PaymentResult.Failure -> {
                    _state.update { it.copy(isProcessing = false) }
                    _events.emit(CheckoutUiEvent.PaymentFailed(orderResult.message))
                }
                is PaymentResult.Cancelled -> {
                    _state.update { it.copy(isProcessing = false) }
                    _events.emit(CheckoutUiEvent.PaymentFailed("Order placement cancelled"))
                }
            }
        }
    }

    fun onPaymobPending() {
        viewModelScope.launch {
            _events.emit(CheckoutUiEvent.PaymentSuccess("Pending", ""))
        }
    }

    fun onPaymobFailure(message: String) {
        viewModelScope.launch {
            _events.emit(CheckoutUiEvent.PaymentFailed(message))
        }
    }

    private fun retryPayment() {
        _state.update {
            it.copy(isProcessing = false)
        }
    }

    private fun calculateShippingFeeCents(city: String, addressText: String): Int {
        val lowerCity = city.trim().lowercase()
        return when {
            lowerCity.contains("cairo") || lowerCity.contains("giza") || lowerCity.contains("القاهرة") || lowerCity.contains("الجيزة") -> 4000
            lowerCity.contains("alexandria") || lowerCity.contains("إسكندرية") || lowerCity.contains("الإسكندرية") -> 7000
            lowerCity.contains("aswan") || lowerCity.contains("luxor") || lowerCity.contains("hurghada") || lowerCity.contains("sinai") ||
            lowerCity.contains("أسوان") || lowerCity.contains("الأقصر") || lowerCity.contains("الغردقة") || lowerCity.contains("سيناء") -> 12000
            else -> if (lowerCity.isNotBlank()) 8000 else 4000
        }
    }

    companion object {
        private const val TAG = "CheckoutViewModel"
    }
}
