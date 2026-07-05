package com.shopify.carto.feature.register.domain.model

enum class RegisterFailureType {
    EmailAlreadyUsed,
    PhoneNumberAlreadyUsed,
    InvalidEmail,
    WeakPassword,
    Network,
    ShopifyConfigurationMissing,
    ShopifySyncFailed,
    FirebaseSyncFailed,
    Unknown,
}
