package com.example.spacetraveler.domain.repository

import com.example.spacetraveler.core.Resource
import com.example.spacetraveler.domain.model.Mission

interface MissionRepository {

    suspend fun getMissionByIdWithFallback(id: Int): Resource<Mission>

    suspend fun createMission(mission: Mission): Resource<Unit>

    suspend fun getAllMissions(): Resource<List<Mission>>

    suspend fun deleteMission(id: Int): Resource<Unit>

}
