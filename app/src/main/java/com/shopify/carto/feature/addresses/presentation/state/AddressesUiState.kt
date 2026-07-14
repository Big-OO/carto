package com.shopify.carto.feature.addresses.presentation.state

import com.shopify.carto.feature.addresses.domain.model.CustomerAddress
import com.shopify.carto.feature.addresses.presentation.model.AddressSnackbarMessage

data class AddressesUiState(
    val isLoading: Boolean = false,
    val isApplying: Boolean = false,
    val removingAddressId: Long? = null,
    val addresses: List<CustomerAddress> = emptyList(),
    val selectedAddressId: Long? = null,
    val initialDefaultAddressId: Long? = null,
    val snackbarMessage: AddressSnackbarMessage? = null,
) {
    val hasDefaultAddressChange: Boolean
        get() = selectedAddressId != null && selectedAddressId != initialDefaultAddressId
}
