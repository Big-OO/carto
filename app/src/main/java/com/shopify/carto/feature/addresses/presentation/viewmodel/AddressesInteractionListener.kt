package com.shopify.carto.feature.addresses.presentation.viewmodel

interface AddressesInteractionListener {
    fun loadAddresses()
    fun selectAddress(addressId: Long)
    fun applyDefaultAddress()
    fun removeAddress(addressId: Long)
    fun consumeSnackbar()
}
