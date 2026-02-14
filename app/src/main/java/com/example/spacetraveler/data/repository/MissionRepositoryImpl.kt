package com.example.spacetraveler.data.repository

import com.example.spacetraveler.core.Resource
import com.example.spacetraveler.core.safeApiResponse
import com.example.spacetraveler.data.local.MissionDao
import com.example.spacetraveler.data.local.MissionEntity
import com.example.spacetraveler.data.local.OfflineOperation
import com.example.spacetraveler.data.local.OfflineOperationDao
import com.example.spacetraveler.data.mapper.MissionMapper
import com.example.spacetraveler.data.remote.MissionApi
import com.example.spacetraveler.data.remote.MissionDto
import com.example.spacetraveler.domain.model.Mission
import com.example.spacetraveler.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor(
    private val api: MissionApi,
    private val offlineDao: OfflineOperationDao,
    private val dao: MissionDao
) : MissionRepository {

    override fun getMissions(): Flow<Result<List<Mission>>> {
        return dao.getAllMissions().map { list ->
            Result.success(list.map { MissionMapper.fromEntityToDomain(it) })
        }
    }

    override suspend fun getMissionById(id: String): Mission? {
        val missionId = id.toIntOrNull() ?: return null
        return dao.getMissionById(missionId)?.let { MissionMapper.fromEntityToDomain(it) }
    }

    override suspend fun createMission(mission: Mission): Result<Unit> {
        val entity = MissionMapper.fromDomainToEntity(mission, isSynced = false)
        val generatedId = dao.insertMission(entity).toInt()
        val entityWithId = entity.copy(id = generatedId)

        return when (val resource = safeApiResponse { api.createMission(MissionMapper.fromEntityToDto(entityWithId)) }) {
            is Resource.Success -> {
                dao.markMissionAsSynced(generatedId)
                Result.success(Unit)
            }
            is Resource.Error -> {
                val operation = OfflineOperation(type = "create", missionId = generatedId)
                offlineDao.insertOperation(operation)
                Result.failure(Exception(resource.message))
            }
            is Resource.Loading -> Result.success(Unit)
        }
    }

    override suspend fun syncPendingMissions(): Result<Unit> {
        return try {
            val pending = dao.getPendingMissions()
            pending.forEach { mission ->
                val resource = safeApiResponse { api.createMission(MissionMapper.fromEntityToDto(mission)) }
                if (resource is Resource.Success) {
                    dao.updateMission(mission.copy(isSynced = true))
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllMissions(): Resource<List<Mission>> {
        val localEntities: List<MissionEntity> = dao.getAllMissions().first()
        val localMissions: List<Mission> = localEntities.map { MissionMapper.fromEntityToDomain(it) }

        return try {
            when (val remote = safeApiResponse { api.getMissions() }) {
                is Resource.Success -> {
                    val body: List<MissionDto> = remote.data
                    val entities = body.map { MissionMapper.fromDtoToEntity(it) }
                    dao.insertMissions(entities)
                    val domainList = entities.map { MissionMapper.fromEntityToDomain(it) }
                    Resource.Success(domainList)
                }
                is Resource.Error -> Resource.Success(localMissions)
                is Resource.Loading -> Resource.Success(localMissions)
            }
        } catch (e: Exception) {
            Resource.Success(localMissions)
        }
    }

    override suspend fun deleteMission(id: Int) {
        dao.deleteMissionById(id)
        val resource = safeApiResponse { api.deleteMission(id) }
        if (resource is Resource.Error) {
            val operation = OfflineOperation(type = "delete", missionId = id)
            offlineDao.insertOperation(operation)
        }
    }
}
