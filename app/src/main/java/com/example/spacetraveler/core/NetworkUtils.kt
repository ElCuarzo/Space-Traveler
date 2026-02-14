package com.example.spacetraveler.core

import retrofit2.Response

suspend fun <T : Any> safeApiResponse(apiCall: suspend () -> Response<T>): Resource<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Resource.Success(body)
            } else {
                Resource.Error(message = "Response body is null", errorType = NetworkError.UNKNOWN)
            }
        } else {
            val errorType = when (response.code()) {
                401 -> NetworkError.UNAUTHORIZED
                404 -> NetworkError.NOT_FOUND
                409 -> NetworkError.CONFLICT
                429 -> NetworkError.TOO_MANY_REQUESTS
                in 500..599 -> NetworkError.SERVER_ERROR
                else -> NetworkError.UNKNOWN
            }
            Resource.Error(message = response.message(), errorType = errorType)
        }
    } catch (e: Exception) {
        Resource.Error(message = e.localizedMessage ?: "Unknown error", errorType = NetworkError.NO_INTERNET)
    }
}


