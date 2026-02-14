package com.example.spacetraveler.domain.repository

import com.example.spacetraveler.data.local.OfflineOperation

interface OfflineOperationsRepository {

    suspend fun syncOfflineOperations(retryDelay: Long = 5000L): List<OfflineOperation>

    suspend fun getPendingOperations(): List<OfflineOperation>

}