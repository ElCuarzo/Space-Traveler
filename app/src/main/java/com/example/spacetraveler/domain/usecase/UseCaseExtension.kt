package com.example.spacetraveler.domain.usecase

import com.example.spacetraveler.core.Resource
import com.example.spacetraveler.core.safeApiCall

suspend fun <R> safeInvoke(block: suspend () -> R): Resource<R> {
    return safeApiCall {
        block()
    }
}