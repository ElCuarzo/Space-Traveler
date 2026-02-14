package com.example.spacetraveler.domain.usecase

import com.example.spacetraveler.domain.repository.MissionRepository

class GetAllMissionsUseCase(private val repository: MissionRepository) {
    suspend operator fun invoke() = repository.getAllMissions()
}
