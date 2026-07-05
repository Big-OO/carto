package com.shopify.carto.feature.addresses.presentation.state

sealed interface NewAddressEffect {
    data object OnNavigateBack : NewAddressEffect
}
