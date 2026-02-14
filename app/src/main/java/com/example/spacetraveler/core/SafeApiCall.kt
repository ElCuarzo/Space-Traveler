package com.example.spacetraveler.core

import retrofit2.HttpException
import java.io.IOException

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): Resource<T> {

    return try {
        val response = apiCall()
        Resource.Success(response)
    } catch (e: HttpException) {

        val errorType = when (e.code()) {
            401 -> NetworkError.UNAUTHORIZED
            404 -> NetworkError.NOT_FOUND
            409 -> NetworkError.CONFLICT
            408 -> NetworkError.REQUEST_TIMEOUT
            429 -> NetworkError.TOO_MANY_REQUESTS
            in 500..599 -> NetworkError.SERVER_ERROR
            else -> NetworkError.UNKNOWN
        }

        Resource.Error(
            message = e.localizedMessage ?: "HTTP error",
            errorType = errorType
        )

    } catch (e: IOException) {

        Resource.Error(
            message = "No internet connection",
            errorType = NetworkError.NO_INTERNET
        )

    } catch (e: Exception) {

        Resource.Error(
            message = e.localizedMessage ?: "Unknown error",
            errorType = NetworkError.UNKNOWN
        )
    }
}
