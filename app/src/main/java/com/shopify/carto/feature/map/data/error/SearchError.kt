package com.shopify.carto.feature.map.data.error

sealed interface SearchError {
    data object UnKnown: SearchError
}