package com.example.carto.feature.addresses.data.remote.mapper

import com.example.carto.feature.addresses.data.remote.error.AddressCreationError
import com.example.carto.feature.addresses.data.remote.error.CustomerIdError
import com.example.carto.feature.addresses.data.remote.error.GetAddressesError
import com.example.carto.feature.addresses.data.remote.error.SetDefaultAddressError
import com.example.carto.feature.addresses.data.result.AddressDataResult
import com.example.carto.feature.addresses.domain.model.AddressFailure
import com.example.carto.feature.addresses.domain.model.AddressFailureType

fun AddressDataResult.Failure<AddressCreationError>.toAddressCreationFailure(): AddressFailure {
    return AddressFailure(
        type = when (error) {
            AddressCreationError.AddressAlreadyExist -> AddressFailureType.AddressAlreadyExist
            AddressCreationError.InvalidCountry -> AddressFailureType.InvalidCountry
            AddressCreationError.InvalidProvince -> AddressFailureType.InvalidProvince
            AddressCreationError.UnKnown,
            null -> AddressFailureType.Unknown
        },
        message = message
    )
}

fun AddressDataResult.Failure<GetAddressesError>.toAddressesFailure(): AddressFailure {
    return AddressFailure(
        type = when (error) {
            GetAddressesError.UnKnown,
            null -> AddressFailureType.Unknown
        },
        message = message
    )
}

fun AddressDataResult.Failure<SetDefaultAddressError>.toDefaultAddressFailure(): AddressFailure {
    return AddressFailure(
        type = when (error) {
            SetDefaultAddressError.UnKnown,
            null -> AddressFailureType.Unknown
        },
        message = message
    )
}