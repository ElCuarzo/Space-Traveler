package com.example.spacetraveler.core

sealed class Resource<out T> {

    data class Success<out T>(
        val data: T
    ) : Resource<T>()

    data class Error(
        val message: String,
        val errorType: NetworkError? = null
    ) : Resource<Nothing>()

    data object Loading : Resource<Nothing>()
}
