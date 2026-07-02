package com.example.carto.feature.addresses.presentation.state

import com.example.carto.feature.addresses.domain.model.AddressForm

data class NewAddressUiState(
    val form: AddressForm = AddressForm(),
    val isSaving: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val errorMessage: String? = null,
) {
    val canSave: Boolean
        get() = form.address1.isNotBlank() && form.phone.isNotBlank()
}
