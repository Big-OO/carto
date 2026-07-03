package com.example.carto.feature.addresses.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carto.feature.addresses.domain.model.AddressFailure
import com.example.carto.feature.addresses.domain.model.AddressFailureType
import com.example.carto.feature.addresses.domain.model.AddressForm
import com.example.carto.feature.addresses.domain.model.AddressResult
import com.example.carto.feature.addresses.domain.usecase.CreateAddressUseCase
import com.example.carto.feature.addresses.presentation.state.NewAddressUiState
import com.example.carto.feature.map.domain.model.SelectedMapAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewAddressViewModel @Inject constructor(
    private val createAddressUseCase: CreateAddressUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(NewAddressUiState())
    val state = _state.asStateFlow()

    fun onNameChanged(value: String) = updateForm { it.copy(name = value) }
    fun onAddressChanged(value: String) = updateForm { it.copy(address1 = value) }
    fun onCityChanged(value: String) = updateForm { it.copy(city = value) }
    fun onProvinceChanged(value: String) = updateForm { it.copy(province = value) }
    fun onCountryChanged(value: String) = updateForm { it.copy(country = value) }
    fun onZipChanged(value: String) = updateForm { it.copy(zip = value) }
    fun onDefaultChanged(value: Boolean) = updateForm { it.copy(isDefault = value) }

    fun onMapAddressSelected(selectedMapAddress: SelectedMapAddress) {
        val address = selectedMapAddress.address
        updateForm {
            it.copy(
                address1 = address.addressLine.ifBlank { it.address1 },
                city = address.city.ifBlank { it.city },
                province = address.province.ifBlank { it.province },
                country = address.country.ifBlank { it.country },
                zip = address.zip.ifBlank { it.zip },
            )
        }
    }

    fun saveAddress() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null) }
            when (val result = createAddressUseCase(state.value.form)) {
                is AddressResult.Success -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            showSuccessDialog = true,
                        )
                    }
                }

                is AddressResult.Failure -> {
                    Log.e(TAG, "Create address failed: ${result.failure.developerMessage}")
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = result.failure.toUserMessage(),
                        )
                    }
                }
            }
        }
    }

    fun dismissSuccess(onDismissed: () -> Unit) {
        _state.update { it.copy(showSuccessDialog = false) }
        onDismissed()
    }

    private fun updateForm(reducer: (AddressForm) -> AddressForm) {
        _state.update { it.copy(form = reducer(it.form), errorMessage = null) }
    }

    private fun AddressFailure.toUserMessage(): String {
        return when (type) {
            AddressFailureType.MissingCustomer -> "Please login again before adding an address."
            AddressFailureType.Validation -> "Please complete the required address details."
            AddressFailureType.NotFound -> "We couldn't find this customer."
            AddressFailureType.Network -> "Check your connection and try again."
            AddressFailureType.Unknown -> "Something went wrong. Try again later."
        }
    }

    companion object {
        private const val TAG = "NewAddressViewModel"
    }
}
