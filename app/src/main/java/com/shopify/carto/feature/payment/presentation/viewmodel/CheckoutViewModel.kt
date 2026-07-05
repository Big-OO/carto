package com.shopify.carto.feature.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.payment.domain.model.PaymentMethod
import com.shopify.carto.feature.payment.domain.model.PaymentRequest
import com.shopify.carto.feature.payment.domain.model.PaymentResult
import com.shopify.carto.feature.payment.domain.usecase.CheckPaymentStatusUseCase
import com.shopify.carto.feature.payment.domain.usecase.CreateCardPaymentUseCase
import com.shopify.carto.feature.payment.domain.usecase.PlaceCashOnDeliveryOrderUseCase
import com.shopify.carto.feature.payment.domain.usecase.ValidateCheckoutUseCase
import com.shopify.carto.feature.payment.domain.usecase.ValidationResult
import com.shopify.carto.feature.payment.presentation.state.CheckoutEvent
import com.shopify.carto.feature.payment.presentation.state.CheckoutUiEvent
import com.shopify.carto.feature.payment.presentation.state.CheckoutUiState
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
    private val validateCheckoutUseCase: ValidateCheckoutUseCase,
    private val checkPaymentStatusUseCase: CheckPaymentStatusUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<CheckoutUiEvent>()
    val events: SharedFlow<CheckoutUiEvent> = _events.asSharedFlow()

    private var activeClientSecret: String? = null

    // use this to verify the payment after transaction Abdelrahman
    suspend fun verifyPaymentStatus(clientSecret: String): Boolean {
        return checkPaymentStatusUseCase(clientSecret)
    }

    // handle this events as you want to match your checkout events Abdelrahman
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
                        validationErrors = it.validationErrors - "address"
                    )
                }

            is CheckoutEvent.UpdateCity ->
                _state.update {
                    it.copy(
                        city = event.value,
                        validationErrors = it.validationErrors - "city"
                    )
                }

            is CheckoutEvent.SelectPaymentMethod ->
                _state.update { it.copy(selectedPaymentMethod = event.method) }

            is CheckoutEvent.PlaceOrder -> placeOrder()
            is CheckoutEvent.RetryPayment -> retryPayment()
        }
    }

    private fun placeOrder() {
        val current = _state.value
        // handle the validation here Abdelrahman ---->
        val validation = validateCheckoutUseCase(
            firstName = current.customerFirstName,
            lastName = current.customerLastName,
            email = current.customerEmail,
            phone = current.customerPhone,
            address = current.address,
            city = current.city,
        )

        when (validation) {
            is ValidationResult.Invalid -> {
                _state.update { it.copy(validationErrors = validation.errors) }
                return
            }

            is ValidationResult.Valid -> {
                _state.update { it.copy(validationErrors = emptyMap()) }
            }
        }

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
        )

        _state.update {
            it.copy(isProcessing = true)
        }

        viewModelScope.launch {
            when (current.selectedPaymentMethod) {
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
                _events.emit(CheckoutUiEvent.PaymentFailed("Payment cancelled"))
            }
        }
    }

    private suspend fun processCodOrder(request: PaymentRequest) {
        when (val result = placeCashOnDeliveryOrderUseCase(request)) {
            is PaymentResult.Success -> {
                _state.update {
                    it.copy(isProcessing = false)
                }
                _events.emit(
                    CheckoutUiEvent.PaymentSuccess(
                        transactionId = result.transactionId,
                        orderId = result.orderId,
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
        val clientSecret = activeClientSecret
        if (clientSecret.isNullOrBlank()) {
            viewModelScope.launch {
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

            _state.update { it.copy(isProcessing = false) }
            if (verified) {
                _events.emit(CheckoutUiEvent.PaymentSuccess(transactionId, ""))
            } else {
                _events.emit(CheckoutUiEvent.PaymentSuccess(transactionId, ""))
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

    companion object {
        private const val TAG = "CheckoutViewModel"
    }
}
