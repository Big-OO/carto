package com.example.carto.feature.addresses.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.feature.addresses.domain.model.AddressFailureType
import com.example.carto.feature.addresses.domain.model.AddressResult
import com.example.carto.feature.addresses.domain.usecase.GetAddressesUseCase
import com.example.carto.feature.addresses.domain.usecase.SetDefaultAddressUseCase
import com.example.carto.feature.addresses.presentation.state.AddressesUiState
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
) : ViewModel() {
    private val _state = MutableStateFlow(AddressesUiState())
    val state = _state.asStateFlow()

    fun loadAddresses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = getAddressesUseCase()) {
                is AddressResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            addresses = result.data,
                            selectedAddressId = result.data.firstOrNull { address -> address.isDefault }?.id
                                ?: result.data.firstOrNull()?.id,
                        )
                    }
                }

                is AddressResult.Failure -> {
                    Log.e(TAG, "Load addresses failed: ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.failure.toUserMessage(),
                        )
                    }
                }
            }
        }
    }

    fun selectAddress(addressId: Long) {
        _state.update { it.copy(selectedAddressId = addressId) }
    }

    fun applyDefaultAddress() {
        val addressId = state.value.selectedAddressId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isApplying = true, errorMessage = null) }
            when (val result = setDefaultAddressUseCase(addressId)) {
                is AddressResult.Success -> {
                    _state.update {
                        it.copy(
                            isApplying = false,
                            addresses = it.addresses.map { address ->
                                address.copy(isDefault = address.id == addressId)
                            },
                            successMessage = "Default address updated.",
                        )
                    }
                }

                is AddressResult.Failure -> {
                    Log.e(TAG, "Set default address failed: ${result.failure.message}")
                    _state.update {
                        it.copy(
                            isApplying = false,
                            errorMessage = result.failure.toUserMessage(),
                        )
                    }
                }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun com.example.carto.feature.addresses.domain.model.AddressFailure.toUserMessage(): String {
        return when (type) {
            AddressFailureType.MissingCustomer -> "Please login again before managing addresses."
            AddressFailureType.Validation -> "Please check the address details and try again."
            AddressFailureType.NotFound -> "We couldn't find this address."
            AddressFailureType.Network -> "Check your connection and try again."
            AddressFailureType.Unknown -> "Something went wrong. Try again later."
            AddressFailureType.InvalidProvince -> TODO()
            AddressFailureType.AddressAlreadyExist -> TODO()
            AddressFailureType.InvalidCountry -> TODO()
        }
    }

    companion object {
        private const val TAG = "AddressesViewModel"
    }
}
