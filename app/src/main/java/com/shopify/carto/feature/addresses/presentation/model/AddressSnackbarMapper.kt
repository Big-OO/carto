package com.shopify.carto.feature.addresses.presentation.model

import androidx.annotation.StringRes
import com.shopify.carto.R
import com.shopify.carto.feature.addresses.domain.model.AddressFailure
import com.shopify.carto.feature.addresses.domain.model.AddressFailureType

fun AddressFailure.toSnackbarMessage(): AddressSnackbarMessage {
    return when (type) {
        AddressFailureType.MissingCustomer -> AddressSnackbarMessage.MissingCustomer
        AddressFailureType.Validation -> AddressSnackbarMessage.Validation
        AddressFailureType.NotFound -> AddressSnackbarMessage.NotFound
        AddressFailureType.Network -> AddressSnackbarMessage.Network
        AddressFailureType.Unauthorized -> AddressSnackbarMessage.Unauthorized
        AddressFailureType.Server -> AddressSnackbarMessage.Server
        AddressFailureType.InvalidProvince -> AddressSnackbarMessage.InvalidProvince
        AddressFailureType.AddressAlreadyExist -> AddressSnackbarMessage.AddressAlreadyExists
        AddressFailureType.InvalidCountry -> AddressSnackbarMessage.InvalidCountry
        AddressFailureType.Unknown -> AddressSnackbarMessage.Unknown
        AddressFailureType.DefaultAddressDeletion -> AddressSnackbarMessage.DefaultAddressDeletion
    }
}

@StringRes
fun AddressSnackbarMessage.stringRes(): Int {
    return when (this) {
        AddressSnackbarMessage.AddressAdded -> R.string.addresses_success_address_added
        AddressSnackbarMessage.AddressRemoved -> R.string.addresses_success_address_removed
        AddressSnackbarMessage.DefaultAddressUpdated -> R.string.addresses_success_default_address_updated
        AddressSnackbarMessage.MissingCustomer -> R.string.addresses_error_missing_customer
        AddressSnackbarMessage.Validation -> R.string.addresses_error_validation
        AddressSnackbarMessage.NotFound -> R.string.addresses_error_not_found
        AddressSnackbarMessage.Network -> R.string.addresses_error_network
        AddressSnackbarMessage.Unauthorized -> R.string.addresses_error_unauthorized
        AddressSnackbarMessage.Server -> R.string.addresses_error_server
        AddressSnackbarMessage.InvalidProvince -> R.string.addresses_error_invalid_province
        AddressSnackbarMessage.AddressAlreadyExists -> R.string.addresses_error_address_already_exists
        AddressSnackbarMessage.InvalidCountry -> R.string.addresses_error_invalid_country
        AddressSnackbarMessage.Unknown -> R.string.addresses_error_unknown
        AddressSnackbarMessage.DefaultAddressDeletion -> R.string.addresses_error_default_address_deletion
    }
}
