package com.shopify.carto.feature.addresses.data.remote.mapper

import com.shopify.carto.feature.addresses.data.remote.error.AddressCreationError
import com.shopify.carto.feature.addresses.data.remote.error.DeleteAddressError
import com.shopify.carto.feature.addresses.data.remote.error.GetAddressesError
import com.shopify.carto.feature.addresses.data.remote.error.SetDefaultAddressError
import com.shopify.carto.feature.addresses.data.result.AddressDataResult
import com.shopify.carto.feature.addresses.domain.model.AddressFailure
import com.shopify.carto.feature.addresses.domain.model.AddressFailureType

fun AddressDataResult.Failure<AddressCreationError>.toAddressCreationFailure(): AddressFailure {
    return AddressFailure(
        type = when (error) {
            AddressCreationError.AddressAlreadyExist -> AddressFailureType.AddressAlreadyExist
            AddressCreationError.InvalidCountry -> AddressFailureType.InvalidCountry
            AddressCreationError.InvalidProvince -> AddressFailureType.InvalidProvince
            AddressCreationError.Unauthorized -> AddressFailureType.Unauthorized
            AddressCreationError.NotFound -> AddressFailureType.NotFound
            AddressCreationError.Validation -> AddressFailureType.Validation
            AddressCreationError.Network -> AddressFailureType.Network
            AddressCreationError.Server -> AddressFailureType.Server
            AddressCreationError.Unknown,
            null -> AddressFailureType.Unknown
        },
        message = message,
    )
}

fun AddressDataResult.Failure<GetAddressesError>.toAddressesFailure(): AddressFailure {
    return AddressFailure(
        type = when (error) {
            GetAddressesError.Unauthorized -> AddressFailureType.Unauthorized
            GetAddressesError.NotFound -> AddressFailureType.NotFound
            GetAddressesError.Network -> AddressFailureType.Network
            GetAddressesError.Server -> AddressFailureType.Server
            GetAddressesError.Unknown,
            null -> AddressFailureType.Unknown
        },
        message = message,
    )
}

fun AddressDataResult.Failure<SetDefaultAddressError>.toDefaultAddressFailure(): AddressFailure {
    return AddressFailure(
        type = when (error) {
            SetDefaultAddressError.Unauthorized -> AddressFailureType.Unauthorized
            SetDefaultAddressError.NotFound -> AddressFailureType.NotFound
            SetDefaultAddressError.Network -> AddressFailureType.Network
            SetDefaultAddressError.Server -> AddressFailureType.Server
            SetDefaultAddressError.Unknown,
            null -> AddressFailureType.Unknown
        },
        message = message,
    )
}

fun AddressDataResult.Failure<DeleteAddressError>.toDeleteAddressFailure(): AddressFailure {
    return AddressFailure(
        type = when (error) {
            DeleteAddressError.Unauthorized -> AddressFailureType.Unauthorized
            DeleteAddressError.NotFound -> AddressFailureType.NotFound
            DeleteAddressError.Network -> AddressFailureType.Network
            DeleteAddressError.Server -> AddressFailureType.Server
            DeleteAddressError.DefaultAddress -> AddressFailureType.DefaultAddressDeletion
            DeleteAddressError.Unknown,
            null -> AddressFailureType.Unknown
        },
        message = message,
    )
}
