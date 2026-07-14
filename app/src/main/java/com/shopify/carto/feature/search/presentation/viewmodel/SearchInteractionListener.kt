package com.shopify.carto.feature.search.presentation.viewmodel

interface SearchInteractionListener {
    fun onSearchValueChanged(newValue: String)
    fun onSearchSubmitted()
    fun onClearSearchClicked()
    fun onHistoryItemClicked(query: String)
    fun onHistoryItemDeleted(id: Long)
    fun onClearHistoryClicked()
    fun onProductClicked(productId: Long)
    fun onProductSuggestionClicked(productId: Long, productTitle: String)
    fun onBackClicked()
}
