package com.example.spacetraveler.domain.usecase

import com.example.spacetraveler.domain.repository.MissionRepository

class SyncMissionsUseCase(private val repository: MissionRepository) {
    suspend operator fun invoke() = repository.syncPendingMissions()
}
