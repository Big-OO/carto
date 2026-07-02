package com.example.carto.feature.addresses.presentation.state

import com.example.carto.feature.addresses.domain.model.CustomerAddress

data class AddressesUiState(
    val isLoading: Boolean = false,
    val isApplying: Boolean = false,
    val addresses: List<CustomerAddress> = emptyList(),
    val selectedAddressId: Long? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
