package com.example.carto.feature.search.data.result

import com.example.carto.feature.search.domain.model.SearchFailure

sealed interface SearchDataResult<out T> {
    data class Success<T>(val data: T) : SearchDataResult<T>
    data class Failure(val failure: SearchFailure) : SearchDataResult<Nothing>
}
