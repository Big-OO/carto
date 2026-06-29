package com.example.carto.registration.data.result

import com.example.carto.registration.domain.model.RegisterFailure

sealed interface RegisterDataResult<out T> {
    data class Success<T>(val data: T) : RegisterDataResult<T>
    data class Failure(val failure: RegisterFailure) : RegisterDataResult<Nothing>
}
