package com.example.spacetraveler.domain.usecase

import com.example.spacetraveler.domain.repository.MissionRepository

class GetMissionsUseCase(private val repository: MissionRepository) {
    operator fun invoke() = repository.getMissions()
}
