package com.example.spacetraveler.core

fun <T> Resource<T>.mapToUnit(): Resource<Unit> = when (this) {
    is Resource.Success -> Resource.Success(Unit)
    is Resource.Error -> Resource.Error(message = this.message, errorType = this.errorType)
    is Resource.Loading -> Resource.Loading
}
