package com.shopify.carto.feature.home_widget.domain.model

data class HomeProfileWidgetData(
    val customerId: String,
    val fullName: String,
    val initials: String,
    val ordersCount: Int,
    val totalPaid: String,
)

sealed interface HomeProfileWidgetState {
    data object Guest : HomeProfileWidgetState
    data object Unavailable : HomeProfileWidgetState
    data class Content(val data: HomeProfileWidgetData) : HomeProfileWidgetState
}
