package com.example.carto.feature.addresses.presentation.state

sealed interface NewAddressEffect {
    data object OnNavigateBack: NewAddressEffect
    data object OnSelectFromMapClick: NewAddressEffect
}