package com.shopify.carto.feature.addresses.presentation.state

import com.shopify.carto.feature.addresses.domain.model.AddressForm
import com.shopify.carto.feature.addresses.presentation.model.AddressSnackbarMessage

data class NewAddressUiState(
    val form: AddressForm = AddressForm(),
    val isSaving: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val snackbarMessage: AddressSnackbarMessage? = null,
) {
    val canSave: Boolean
        get() = form.name.isNotBlank() &&
            form.address1.isNotBlank() &&
            form.city.isNotBlank() &&
            form.province.isNotBlank() &&
            form.country.isNotBlank() &&
            form.zip.isNotBlank()
}
