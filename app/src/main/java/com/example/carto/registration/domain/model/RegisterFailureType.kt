package com.example.carto.registration.domain.model

enum class RegisterFailureType {
    EmailAlreadyUsed,
    InvalidEmail,
    WeakPassword,
    Network,
    ShopifyConfigurationMissing,
    ShopifySyncFailed,
    FirebaseSyncFailed,
    Unknown,
}
