package com.shopify.carto.feature.orderhistory.presentation.viewmodel

import com.shopify.carto.feature.orderhistory.presentation.model.OrderHistoryTabUi

interface OrderHistoryInteractionListener {
    fun onTabClicked(tab: OrderHistoryTabUi)
    fun onOrderClicked(orderId: String)
    fun onRetryClicked()
}
