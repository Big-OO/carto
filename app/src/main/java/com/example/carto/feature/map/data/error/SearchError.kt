package com.example.carto.feature.map.data.error

sealed interface SearchError {
    data object UnKnown: SearchError
}