package com.example.carto.feature.addresses.presentation.viewmodel

import com.example.carto.feature.map.domain.model.SelectedMapAddress

interface NewAddressInteractionListener {
    fun onNameChanged(value: String)
    fun onAddressChanged(value: String)
    fun onCityChanged(value: String)
    fun onProvinceChanged(value: String)
    fun onCountryChanged(value: String)
    fun onZipChanged(value: String)
    fun onDefaultChanged(value: Boolean)
    fun onMapAddressSelected(selectedMapAddress: SelectedMapAddress)
    fun saveAddress()
    fun onNavigateBack()
}
