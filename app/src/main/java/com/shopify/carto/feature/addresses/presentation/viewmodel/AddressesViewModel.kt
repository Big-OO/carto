package com.shopify.carto.feature.addresses.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.carto.feature.addresses.domain.model.AddressResult
import com.shopify.carto.feature.addresses.domain.usecase.DeleteAddressUseCase
import com.shopify.carto.feature.addresses.domain.usecase.GetAddressesUseCase
import com.shopify.carto.feature.addresses.domain.usecase.SetDefaultAddressUseCase
import com.shopify.carto.feature.addresses.presentation.model.AddressSnackbarMessage
import com.shopify.carto.feature.addresses.presentation.model.toSnackbarMessage
import com.shopify.carto.feature.addresses.presentation.state.AddressesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressesViewModel @Inject constructor(
    private val getAddressesUseCase: GetAddressesUseCase,
    private val setDefaultAddressUseCase: SetDefaultAddressUseCase,
    private val deleteAddressUseCase: DeleteAddressUseCase,
) : ViewModel(), AddressesInteractionListener {
    private val _state = MutableStateFlow(AddressesUiState())
    val state = _state.asStateFlow()

    override fun loadAddresses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, snackbarMessage = null) }
            when (val result = getAddressesUseCase()) {
                is AddressResult.Success -> {
                    val defaultId = result.data.firstOrNull { address -> address.isDefault }?.id
                    _state.update {
                        it.copy(
                            isLoading = false,
                            addresses = result.data,
                            selectedAddressId = defaultId ?: result.data.firstOrNull()?.id,
                            initialDefaultAddressId = defaultId,
                        )
                    }
                }

                is AddressResult.Failure -> {
                    Log.e(TAG, "Load addresses failed: ${result.failure.type}, ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            snackbarMessage = result.failure.toSnackbarMessage(),
                        )
                    }
                }
            }
        }
    }

    override fun selectAddress(addressId: Long) {
        _state.update { it.copy(selectedAddressId = addressId) }
    }

    override fun applyDefaultAddress() {
        val addressId = state.value.selectedAddressId ?: return
        if (!state.value.hasDefaultAddressChange) return

        viewModelScope.launch {
            _state.update { it.copy(isApplying = true, snackbarMessage = null) }
            when (val result = setDefaultAddressUseCase(addressId)) {
                is AddressResult.Success -> {
                    _state.update {
                        it.copy(
                            isApplying = false,
                            initialDefaultAddressId = addressId,
                            addresses = it.addresses.map { address ->
                                address.copy(isDefault = address.id == addressId)
                            },
                            snackbarMessage = AddressSnackbarMessage.DefaultAddressUpdated,
                        )
                    }
                }

                is AddressResult.Failure -> {
                    Log.e(TAG, "Set default address failed: ${result.failure.type}, ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isApplying = false,
                            snackbarMessage = result.failure.toSnackbarMessage(),
                        )
                    }
                }
            }
        }
    }

    override fun removeAddress(addressId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(removingAddressId = addressId, snackbarMessage = null) }
            when (val result = deleteAddressUseCase(addressId)) {
                is AddressResult.Success -> {
                    _state.update { current ->
                        val updatedAddresses = current.addresses.filterNot { it.id == addressId }
                        val removedDefault = current.initialDefaultAddressId == addressId
                        val updatedDefaultId = if (removedDefault) {
                            updatedAddresses.firstOrNull { it.isDefault }?.id
                        } else {
                            current.initialDefaultAddressId
                        }
                        val updatedSelectedId = when {
                            current.selectedAddressId != addressId -> current.selectedAddressId
                            updatedDefaultId != null -> updatedDefaultId
                            else -> updatedAddresses.firstOrNull()?.id
                        }

                        current.copy(
                            removingAddressId = null,
                            addresses = updatedAddresses,
                            selectedAddressId = updatedSelectedId,
                            initialDefaultAddressId = updatedDefaultId,
                            snackbarMessage = AddressSnackbarMessage.AddressRemoved,
                        )
                    }
                }

                is AddressResult.Failure -> {
                    Log.e(TAG, "Delete address failed: ${result.failure.type}, ${result.failure.message}")
                    _state.update {
                        it.copy(
                            removingAddressId = null,
                            snackbarMessage = result.failure.toSnackbarMessage(),
                        )
                    }
                }
            }
        }
    }

    override fun consumeSnackbar() {
        _state.update { it.copy(snackbarMessage = null) }
    }

    companion object {
        private const val TAG = "AddressesViewModel"
    }
}
