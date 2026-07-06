package com.shopify.carto.feature.orderhistory.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.core.session.domain.usecase.ObserveAppSessionUseCase
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryFailureType
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryResult
import com.shopify.carto.feature.orderhistory.domain.usecase.GetCustomerOrdersUseCase
import com.shopify.carto.feature.orderhistory.domain.usecase.ObserveHiddenOrderIdsUseCase
import com.shopify.carto.feature.orderhistory.presentation.mapper.toUi
import com.shopify.carto.feature.orderhistory.presentation.model.OrderHistoryItemUi
import com.shopify.carto.feature.orderhistory.presentation.model.OrderHistoryTabUi
import com.shopify.carto.feature.orderhistory.presentation.state.OrderHistoryErrorType
import com.shopify.carto.feature.orderhistory.presentation.state.OrderHistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val observeAppSessionUseCase: ObserveAppSessionUseCase,
    private val getCustomerOrdersUseCase: GetCustomerOrdersUseCase,
    private val observeHiddenOrderIdsUseCase: ObserveHiddenOrderIdsUseCase,
) : ViewModel(), OrderHistoryInteractionListener {

    private val _state = MutableStateFlow(OrderHistoryUiState(isLoading = true))
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<OrderHistoryEffect>()
    val effects = _effects.asSharedFlow()

    private var allOrders = emptyList<OrderHistoryItemUi>()
    private var hiddenOrderIds = emptySet<String>()

    init {
        observeHiddenOrders()
        loadOrders()
    }

    override fun onBackClicked() {
        viewModelScope.launch {
            _effects.emit(OrderHistoryEffect.NavigateBack)
        }
    }

    override fun onTabClicked(tab: OrderHistoryTabUi) {
        _state.update { it.copy(selectedTab = tab) }
    }

    override fun onOrderClicked(orderId: String) {
        viewModelScope.launch {
            _effects.emit(OrderHistoryEffect.NavigateToOrderDetails(orderId))
        }
    }

    override fun onRetryClicked() {
        loadOrders()
    }

    private fun observeHiddenOrders() {
        viewModelScope.launch {
            observeHiddenOrderIdsUseCase().collect { ids ->
                hiddenOrderIds = ids
                applyHiddenOrders()
            }
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val session = observeAppSessionUseCase().first()
            val customerId = session.customerId?.toLongOrNull()
            if (session.isGuest || customerId == null) {
                val errorType = OrderHistoryErrorType.MissingCustomer
                _state.update { it.copy(isLoading = false, error = errorType, orders = emptyList()) }
                _effects.emit(OrderHistoryEffect.ShowError(errorType))
                return@launch
            }

            when (val result = getCustomerOrdersUseCase(customerId)) {
                is OrderHistoryResult.Success -> {
                    allOrders = result.data.map { it.toUi() }
                    applyHiddenOrders()
                    _state.update { it.copy(isLoading = false, error = null) }
                }

                is OrderHistoryResult.Failure -> {
                    Log.e(TAG, result.failure.message)
                    val errorType = result.failure.type.toUiError()
                    _state.update { it.copy(isLoading = false, error = errorType, orders = emptyList()) }
                    _effects.emit(OrderHistoryEffect.ShowError(errorType))
                }
            }
        }
    }

    private fun applyHiddenOrders() {
        _state.update { current ->
            current.copy(orders = allOrders.filterNot { it.id in hiddenOrderIds })
        }
    }

    private fun OrderHistoryFailureType.toUiError(): OrderHistoryErrorType {
        return when (this) {
            OrderHistoryFailureType.MissingCustomer -> OrderHistoryErrorType.MissingCustomer
            OrderHistoryFailureType.ShopifyConfigurationMissing -> OrderHistoryErrorType.Configuration
            OrderHistoryFailureType.Unauthorized -> OrderHistoryErrorType.Unauthorized
            OrderHistoryFailureType.Network -> OrderHistoryErrorType.Network
            OrderHistoryFailureType.Server -> OrderHistoryErrorType.Server
            OrderHistoryFailureType.NotFound -> OrderHistoryErrorType.NotFound
            OrderHistoryFailureType.GraphQl,
            OrderHistoryFailureType.Unknown -> OrderHistoryErrorType.Unknown
        }
    }

    private companion object {
        const val TAG = "OrderHistoryViewModel"
    }
}
