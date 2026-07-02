package com.example.carto.feature.home.data.remote


import retrofit2.Response

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): Result<T> {

    return try {

        val response = apiCall()

        if (response.isSuccessful) {

            response.body()?.let {

                Result.success(it)

            } ?: Result.failure(
                Exception("Response body is empty.")
            )

        } else {

            Result.failure(
                Exception(
                    "HTTP ${response.code()}: ${response.message()}"
                )
            )

        }

    } catch (e: Exception) {

        Result.failure(e)

    }

}