package com.shopify.carto.feature.product_details.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.R
import com.shopify.carto.core.session.domain.model.AppSession
import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.shopify.carto.core.utils.toUiErrorMessage
import com.shopify.carto.feature.favorite.domain.usecase.ObserveFavoritesUseCase
import com.shopify.carto.feature.favorite.domain.usecase.ToggleFavoriteUseCase
import com.shopify.carto.feature.product_details.domain.model.merchandiseId
import com.shopify.carto.feature.product_details.domain.usecase.GetProductDetailsUseCase
import com.shopify.carto.feature.product_details.domain.usecase.AddToCartUseCase
import com.shopify.carto.feature.product_details.domain.usecase.RemoveFromCartUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.ObserveCartUseCase
import com.shopify.carto.feature.shopping_cart.domain.usecase.RefreshCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val observeFavoriteUseCase: ObserveFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val observeCartUseCase: ObserveCartUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val refreshCartUseCase: RefreshCartUseCase,
    private val observeAppSessionUseCase: ObserveAppSessionUseCase
) : ViewModel() {

    private val productIdFlow = MutableStateFlow<Long?>(null)
    private val currentSessionFlow = MutableStateFlow(AppSession())

    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    private val _effect = Channel<ProductDetailsEffect>()
    val effect: Flow<ProductDetailsEffect> = _effect.receiveAsFlow()

    init {
        observeSessionState()
        observeFavoriteState()
        observeCartState()
    }

    fun onEvent(event: ProductDetailsEvent) {
        when (event) {
            ProductDetailsEvent.OnBackClick -> sendEffect(ProductDetailsEffect.NavigateBack)
            ProductDetailsEvent.OnFavoriteClick -> onFavoriteClick()
            ProductDetailsEvent.OnAddToCartClick -> onAddToCartClick()
            ProductDetailsEvent.OnRemoveFromCartConfirm -> onRemoveFromCartConfirm()
            ProductDetailsEvent.OnRetryClick -> productIdFlow.value?.let { loadProductDetails(it.toString()) }
            is ProductDetailsEvent.OnImageSelected -> _uiState.update { it.copy(selectedImageIndex = event.index) }
            is ProductDetailsEvent.OnSizeSelected -> _uiState.update { it.copy(selectedSize = event.size) }
            is ProductDetailsEvent.OnColorSelected -> _uiState.update { it.copy(selectedColor = event.color) }
        }
    }

    fun loadProductDetails(productId: String) {
        val id = productId.toLong()
        productIdFlow.value = id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            refreshCartUseCase()
            getProductDetailsUseCase(id)
                .onSuccess { product ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            product = product,
                            selectedSize = product.sizes.firstOrNull(),
                            selectedColor = product.colors.firstOrNull()
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                    sendEffect(ProductDetailsEffect.ShowError(throwable.toUiErrorMessage()))
                }
        }
    }

    private fun onFavoriteClick() {
        val session = currentSessionFlow.value

        if (session.isGuest || !session.isLoggedIn) {
            sendEffect(ProductDetailsEffect.ShowError(R.string.login_required))
            return
        }

        val product = _uiState.value.product ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(
                productId = product.id,
                name = product.title,
                imageUrl = product.images.firstOrNull(),
                price = product.price
            )
        }
    }

    private fun onAddToCartClick() {
        val session = currentSessionFlow.value

        if (session.isGuest || !session.isLoggedIn) {
            sendEffect(ProductDetailsEffect.ShowError(R.string.login_required))
            return
        }

        val state = _uiState.value
        val variant = state.selectedVariant ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingToCart = true) }
            addToCartUseCase(variant.merchandiseId, quantity = 1)
                .onSuccess { _uiState.update { it.copy(isAddingToCart = false, isInCart = true) } }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isAddingToCart = false) }
                    Log.d("test","${throwable.message}")
                    sendEffect(ProductDetailsEffect.ShowError(throwable.toUiErrorMessage()))
                }
        }
    }

    private fun onRemoveFromCartConfirm() {
        val variant = _uiState.value.selectedVariant ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingToCart = true) }

            val cartResult = observeCartUseCase().first()
            val lineId = cartResult.getOrNull()?.lines?.find { it.merchandiseId == variant.merchandiseId }?.id

            if (lineId != null) {
                removeFromCartUseCase(lineId)
                    .onSuccess {
                        _uiState.update { it.copy(isAddingToCart = false, isInCart = false) }
                    }
                    .onFailure { throwable ->
                        _uiState.update { it.copy(isAddingToCart = false) }
                        sendEffect(ProductDetailsEffect.ShowError(throwable.toUiErrorMessage()))
                    }
            } else {
                _uiState.update { it.copy(isAddingToCart = false) }
            }
        }
    }

    private fun observeSessionState() {
        observeAppSessionUseCase()
            .onEach { session -> currentSessionFlow.value = session }
            .launchIn(viewModelScope)
    }

    private fun observeFavoriteState() {
        combine(
            productIdFlow.filterNotNull(),
            observeFavoriteUseCase()
        ) { id, favorites ->
            favorites.any { it.productId == id }
        }
            .distinctUntilChanged()
            .onEach { isFavorite -> _uiState.update { it.copy(isFavorite = isFavorite) } }
            .launchIn(viewModelScope)
    }

    private fun observeCartState() {
        combine(observeCartUseCase(), _uiState) { cartResult, state ->
            cartResult.getOrNull()?.lines.orEmpty().any { it.merchandiseId == state.selectedVariant?.merchandiseId }
        }
            .distinctUntilChanged()
            .onEach { isInCart -> _uiState.update { it.copy(isInCart = isInCart) } }
            .launchIn(viewModelScope)
    }

    private fun sendEffect(effect: ProductDetailsEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}