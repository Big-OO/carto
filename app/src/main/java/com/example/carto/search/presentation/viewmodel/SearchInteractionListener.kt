package com.example.carto.search.presentation.viewmodel

interface SearchInteractionListener {
    fun onSearchValueChanged(newValue: String)
    fun onSearchSubmitted()
    fun onHistoryItemClicked(query: String)
    fun onHistoryItemDeleted(id: Long)
    fun onClearHistoryClicked()
    fun onProductClicked(productId: Long)
    fun onBackClicked()
}
