package com.example.carto.feature.register.data.result

import com.example.carto.feature.register.domain.model.RegisterFailure

sealed interface RegisterDataResult<out T> {
    data class Success<T>(val data: T) : RegisterDataResult<T>
    data class Failure(val failure: RegisterFailure) : RegisterDataResult<Nothing>
}
