package com.shopify.carto.feature.home.data.remote



import com.shopify.carto.core.utils.AppError
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): Result<T> {

    return try {

        val response = apiCall()

        if (response.isSuccessful) {

            response.body()?.let {

                Result.success(it)

            } ?: Result.failure(
                AppError.Unknown("Response body is empty.")
            )

        } else {

            Result.failure(
                AppError.Server(response.code())
            )

        }

    } catch (e: UnknownHostException) {

        Result.failure(AppError.NoInternet)

    } catch (e: SocketTimeoutException) {

        Result.failure(AppError.Timeout)

    } catch (e: IOException) {

        // Covers ConnectException, SSLException, and other connectivity issues.
        Result.failure(AppError.NoInternet)

    } catch (e: Exception) {

        Result.failure(AppError.Unknown(e.message))

    }

}

//import retrofit2.Response
//
//suspend fun <T> safeApiCall(
//    apiCall: suspend () -> Response<T>
//): Result<T> {
//
//    return try {
//
//        val response = apiCall()
//
//        if (response.isSuccessful) {
//
//            response.body()?.let {
//
//                Result.success(it)
//
//            } ?: Result.failure(
//                Exception("Response body is empty.")
//            )
//
//        } else {
//
//            Result.failure(
//                Exception(
//                    "HTTP ${response.code()}: ${response.message()}"
//                )
//            )
//
//        }
//
//    } catch (e: Exception) {
//
//        Result.failure(e)
//
//    }
//
//}