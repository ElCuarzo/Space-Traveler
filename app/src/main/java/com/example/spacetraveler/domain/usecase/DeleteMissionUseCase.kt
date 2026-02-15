package com.example.spacetraveler.domain.usecase

import com.example.spacetraveler.core.Resource
import com.example.spacetraveler.domain.repository.MissionRepository

class DeleteMissionUseCase(private val repository: MissionRepository) {
    suspend operator fun invoke(id: Int): Resource<Unit> {
        return repository.deleteMission(id)
    }
}
