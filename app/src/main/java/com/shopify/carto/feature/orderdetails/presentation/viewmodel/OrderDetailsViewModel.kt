package com.shopify.carto.feature.orderdetails.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsFailureType
import com.shopify.carto.feature.orderdetails.domain.model.OrderDetailsResult
import com.shopify.carto.feature.orderdetails.domain.usecase.CancelOrderUseCase
import com.shopify.carto.feature.orderdetails.domain.usecase.GetOrderDetailsUseCase
import com.shopify.carto.feature.orderdetails.presentation.mapper.toUi
import com.shopify.carto.feature.orderdetails.presentation.state.OrderDetailsDialog
import com.shopify.carto.feature.orderdetails.presentation.state.OrderDetailsErrorType
import com.shopify.carto.feature.orderdetails.presentation.state.OrderDetailsUiState
import com.shopify.carto.feature.orderhistory.domain.model.OrderHistoryResult
import com.shopify.carto.feature.orderhistory.domain.usecase.HideOrderFromHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getOrderDetailsUseCase: GetOrderDetailsUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val hideOrderFromHistoryUseCase: HideOrderFromHistoryUseCase,
) : ViewModel(), OrderDetailsInteractionListener {

    private val orderId: String = Uri.decode(checkNotNull(savedStateHandle.get<String>(ORDER_ID_ARGUMENT)))

    private val _state = MutableStateFlow(OrderDetailsUiState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<OrderDetailsEffect>()
    val effects = _effects.asSharedFlow()

    init {
        loadOrderDetails()
    }

    override fun onBackClicked() {
        viewModelScope.launch { _effects.emit(OrderDetailsEffect.NavigateBack) }
    }

    override fun onRetryClicked() {
        loadOrderDetails()
    }

    override fun onCancelOrderClicked() {
        _state.update { it.copy(pendingDialog = OrderDetailsDialog.CancelOrder) }
    }

    override fun onHideOrderClicked() {
        _state.update { it.copy(pendingDialog = OrderDetailsDialog.HideOrder) }
    }

    override fun onDialogDismissed() {
        _state.update { it.copy(pendingDialog = null) }
    }

    override fun onDialogConfirmed() {
        when (_state.value.pendingDialog) {
            OrderDetailsDialog.CancelOrder -> cancelOrder()
            OrderDetailsDialog.HideOrder -> hideOrder()
            null -> Unit
        }
    }

    private fun loadOrderDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getOrderDetailsUseCase(orderId)) {
                is OrderDetailsResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            order = result.data.toUi(),
                            error = null,
                        )
                    }
                }

                is OrderDetailsResult.Failure -> {
                    Log.e(TAG, result.failure.message)
                    val errorType = result.failure.type.toUiError()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = errorType,
                            order = null,
                        )
                    }
                    _effects.emit(OrderDetailsEffect.ShowError(errorType))
                }
            }
        }
    }

    private fun cancelOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isProcessingAction = true, pendingDialog = null) }
            when (val result = cancelOrderUseCase(orderId)) {
                is OrderDetailsResult.Success -> {
                    _state.update { it.copy(isProcessingAction = false) }
                    _effects.emit(OrderDetailsEffect.ShowOrderCancelled)
                    loadOrderDetails()
                }

                is OrderDetailsResult.Failure -> {
                    Log.e(TAG, result.failure.message)
                    val errorType = result.failure.type.toUiError()
                    _state.update { it.copy(isProcessingAction = false) }
                    _effects.emit(OrderDetailsEffect.ShowError(errorType))
                }
            }
        }
    }

    private fun hideOrder() {
        viewModelScope.launch {
            _state.update { it.copy(isProcessingAction = true, pendingDialog = null) }
            when (val result = hideOrderFromHistoryUseCase(orderId)) {
                is OrderHistoryResult.Success -> {
                    _state.update { it.copy(isProcessingAction = false) }
                    _effects.emit(OrderDetailsEffect.ShowOrderRemoved)
                    _effects.emit(OrderDetailsEffect.NavigateBack)
                }

                is OrderHistoryResult.Failure -> {
                    Log.e(TAG, result.failure.message)
                    _state.update { it.copy(isProcessingAction = false) }
                    _effects.emit(OrderDetailsEffect.ShowError(OrderDetailsErrorType.Unknown))
                }
            }
        }
    }

    private fun OrderDetailsFailureType.toUiError(): OrderDetailsErrorType {
        return when (this) {
            OrderDetailsFailureType.ShopifyConfigurationMissing -> OrderDetailsErrorType.Configuration
            OrderDetailsFailureType.Unauthorized -> OrderDetailsErrorType.Unauthorized
            OrderDetailsFailureType.Network -> OrderDetailsErrorType.Network
            OrderDetailsFailureType.Server -> OrderDetailsErrorType.Server
            OrderDetailsFailureType.NotFound -> OrderDetailsErrorType.NotFound
            OrderDetailsFailureType.GraphQl,
            OrderDetailsFailureType.Unknown -> OrderDetailsErrorType.Unknown
        }
    }

    private companion object {
        const val TAG = "OrderDetailsViewModel"
        const val ORDER_ID_ARGUMENT = "orderId"
    }
}
