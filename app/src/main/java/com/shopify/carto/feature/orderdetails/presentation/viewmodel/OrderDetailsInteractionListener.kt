package com.shopify.carto.feature.orderdetails.presentation.viewmodel

interface OrderDetailsInteractionListener {
    fun onBackClicked()
    fun onRetryClicked()
    fun onCancelOrderClicked()
    fun onHideOrderClicked()
    fun onDialogDismissed()
    fun onDialogConfirmed()
}
