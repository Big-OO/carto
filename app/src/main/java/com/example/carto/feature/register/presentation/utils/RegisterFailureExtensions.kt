package com.example.carto.feature.register.presentation.utils

import com.example.carto.feature.register.domain.model.RegisterFailure
import com.example.carto.feature.register.domain.model.RegisterFailureType


fun RegisterFailure.toUserMessage(): String {
    return when (type) {
        RegisterFailureType.EmailAlreadyUsed -> "This email is already registered. Try logging in."
        RegisterFailureType.InvalidEmail -> "Please enter a valid email address."
        RegisterFailureType.WeakPassword -> "Please choose a stronger password."
        RegisterFailureType.Network -> "Check your internet connection and try again."
        RegisterFailureType.ShopifyConfigurationMissing,
        RegisterFailureType.ShopifySyncFailed,
        RegisterFailureType.FirebaseSyncFailed,
        RegisterFailureType.Unknown -> "We couldn't create your account right now. Try again later."
    }
}