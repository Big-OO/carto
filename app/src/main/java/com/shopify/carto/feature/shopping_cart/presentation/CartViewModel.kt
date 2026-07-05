package com.shopify.carto.feature.shopping_cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.core.utils.toUiErrorMessage
import com.shopify.carto.feature.shopping_cart.domain.usecase.ObserveCartUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.RefreshCartUseCase
import com.shopify.carto.feature.product_details.domain.usecase.RemoveFromCartUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.UpdateCartQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val refreshCartUseCase: RefreshCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState(isLoading = true))
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private val _effect = Channel<CartEffect>()
    val effect: Flow<CartEffect> = _effect.receiveAsFlow()

    init {
        observeCart()
        viewModelScope.launch { refreshCartUseCase() }
    }

    fun onEvent(event: CartEvent) {
        when (event) {
            CartEvent.OnCheckoutClick -> onCheckoutClick()
            is CartEvent.OnIncreaseQuantity -> updateQuantity(event.lineId, event.currentQuantity + 1)
            is CartEvent.OnDecreaseQuantity -> updateQuantity(event.lineId, event.currentQuantity - 1)
            is CartEvent.OnRemoveLine -> removeLine(event.lineId)
        }
    }

    private fun observeCart() {
        observeCartUseCase()
            .onEach { result ->
                result
                    .onSuccess { cart -> _uiState.update { it.copy(isLoading = false, cart = cart, errorMessage = null) } }
                    .onFailure { throwable ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                        sendEffect(CartEffect.ShowError(throwable.toUiErrorMessage()))
                    }
            }
            .launchIn(viewModelScope)
    }

    private fun onCheckoutClick() {
        val checkoutUrl = _uiState.value.cart?.checkoutUrl ?: return
        sendEffect(CartEffect.NavigateToCheckout(checkoutUrl))
    }

    private fun updateQuantity(lineId: String, quantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(updatingLineIds = it.updatingLineIds + lineId) }
            updateCartQuantityUseCase(lineId, quantity)
                .onFailure { throwable -> sendEffect(CartEffect.ShowError(throwable.toUiErrorMessage())) }
            _uiState.update { it.copy(updatingLineIds = it.updatingLineIds - lineId) }
        }
    }

    private fun removeLine(lineId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(updatingLineIds = it.updatingLineIds + lineId) }
            removeFromCartUseCase(lineId)
                .onFailure { throwable -> sendEffect(CartEffect.ShowError(throwable.toUiErrorMessage())) }
            _uiState.update { it.copy(updatingLineIds = it.updatingLineIds - lineId) }
        }
    }

    private fun sendEffect(effect: CartEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}