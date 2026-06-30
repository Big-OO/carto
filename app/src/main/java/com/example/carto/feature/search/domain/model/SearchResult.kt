package com.example.carto.feature.search.domain.model

sealed interface SearchResult<out T> {
    data class Success<T>(val data: T) : SearchResult<T>
    data class Failure(val failure: SearchFailure) : SearchResult<Nothing>
}
