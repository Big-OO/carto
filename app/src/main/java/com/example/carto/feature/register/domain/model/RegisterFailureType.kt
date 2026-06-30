package com.example.carto.feature.register.domain.model

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
