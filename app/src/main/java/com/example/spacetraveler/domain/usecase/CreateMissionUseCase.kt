package com.example.spacetraveler.domain.usecase

import com.example.spacetraveler.domain.model.Mission
import com.example.spacetraveler.domain.repository.MissionRepository

class CreateMissionUseCase(private val repository: MissionRepository) {
    suspend operator fun invoke(mission: Mission) = repository.createMission(mission)
}