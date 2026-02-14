package com.example.spacetraveler.domain.usecase

import com.example.spacetraveler.domain.repository.MissionRepository

class GetMissionDetailUseCase(private val repository: MissionRepository) {
    suspend operator fun invoke(id: String) = repository.getMissionById(id)
}
