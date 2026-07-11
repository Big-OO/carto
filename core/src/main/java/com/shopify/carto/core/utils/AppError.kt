package com.shopify.carto.core.utils

sealed class AppError(override val message: String) : Exception(message) {

    data object NoInternet : AppError("No internet connection")

    data object Timeout : AppError("Request timed out")

    data class Server(val code: Int) : AppError("Server error ($code)")

    data class Unknown(val raw: String? = null) : AppError(raw ?: "Unknown error")
}
