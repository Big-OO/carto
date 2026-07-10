package com.shopify.carto.core.common.exception

sealed class DataException(cause: Throwable? = null) : Exception(cause) {
    class NotFound(val resourceId: String? = null) : DataException()
    class Network(cause: Throwable) : DataException(cause)
    class Server(cause: Throwable) : DataException(cause)
    class Unknown(cause: Throwable) : DataException(cause)
}